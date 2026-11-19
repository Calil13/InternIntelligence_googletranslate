package org.example.googletranslate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.googletranslate.dto.TranslationRequest;
import org.example.googletranslate.dto.TranslationResponse;
import org.example.googletranslate.model.AppSettings;
import org.example.googletranslate.model.SavedTranslation;
import org.example.googletranslate.model.TranslationHistory;
import org.example.googletranslate.model.UserProfile;
import org.example.googletranslate.repository.AppSettingsRepository;
import org.example.googletranslate.repository.SavedTranslationRepository;
import org.example.googletranslate.repository.TranslationHistoryRepository;
import org.example.googletranslate.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TranslationService {

    private final TranslationHistoryRepository historyRepository;
    private final SavedTranslationRepository savedRepository;
    private final UserProfileRepository userProfileRepository;
    private final AppSettingsRepository settingsRepository;
    private final LanguageService languageService;
    private final ObjectMapper objectMapper;

    // Built-in offline dictionary fallback
    private final Map<String, Map<String, String>> dictionary = new HashMap<>();

    public TranslationService(TranslationHistoryRepository historyRepository,
                              SavedTranslationRepository savedRepository,
                              UserProfileRepository userProfileRepository,
                              AppSettingsRepository settingsRepository,
                              LanguageService languageService) {
        this.historyRepository = historyRepository;
        this.savedRepository = savedRepository;
        this.userProfileRepository = userProfileRepository;
        this.settingsRepository = settingsRepository;
        this.languageService = languageService;
        this.objectMapper = new ObjectMapper();
        initDictionary();
    }

    public TranslationResponse translate(TranslationRequest request) {
        String originalText = request.getText() != null ? request.getText().trim() : "";
        if (originalText.isEmpty()) {
            return new TranslationResponse(
                    request.getSourceLanguage(),
                    request.getTargetLanguage(),
                    request.getSourceLanguage(),
                    "",
                    "",
                    null,
                    false,
                    "",
                    "",
                    Collections.emptyList()
            );
        }

        String targetLang = request.getTargetLanguage() != null ? request.getTargetLanguage().toLowerCase() : "en";
        String reqSourceLang = request.getSourceLanguage() != null ? request.getSourceLanguage().toLowerCase() : "auto";

        String detectedLang = reqSourceLang;
        if ("auto".equalsIgnoreCase(reqSourceLang)) {
            detectedLang = detectLanguageLocally(originalText);
        }

        String translatedText = null;
        String pronunciation = "";
        String translatedPronunciation = "";
        List<String> alternatives = new ArrayList<>();

        // 1. Try Google Translate GTX API
        try {
            GtxResult gtxResult = translateViaGtx(originalText, reqSourceLang, targetLang);
            if (gtxResult != null && gtxResult.translatedText != null && !gtxResult.translatedText.isBlank()) {
                translatedText = gtxResult.translatedText;
                if ("auto".equalsIgnoreCase(reqSourceLang) && gtxResult.detectedLang != null && !gtxResult.detectedLang.isBlank()) {
                    detectedLang = gtxResult.detectedLang;
                }
                if (gtxResult.pronunciation != null) {
                    translatedPronunciation = gtxResult.pronunciation;
                }
            }
        } catch (Exception e) {
            // fallback to next method
        }

        // 2. Try MyMemory API if still null
        if (translatedText == null) {
            try {
                String myMemoryResult = translateViaMyMemory(originalText, detectedLang, targetLang);
                if (myMemoryResult != null && !myMemoryResult.isBlank()) {
                    translatedText = myMemoryResult;
                }
            } catch (Exception e) {
                // fallback to dictionary
            }
        }

        // 3. Fallback to offline dictionary
        if (translatedText == null) {
            translatedText = translateViaDictionary(originalText, detectedLang, targetLang);
        }

        // If same language or could not translate, preserve original
        if (translatedText == null || translatedText.isBlank()) {
            translatedText = originalText;
        }

        // Check if saved
        boolean isSaved = savedRepository.existsByOriginalTextAndSourceLanguageAndTargetLanguage(
                originalText, detectedLang, targetLang
        );

        // History recording (if enabled in settings)
        Long historyId = null;
        AppSettings settings = settingsRepository.findFirstByOrderByIdAsc()
                .orElseGet(AppSettings::defaultSettings);

        if (settings.isHistoryEnabled()) {
            TranslationHistory history = new TranslationHistory(
                    detectedLang,
                    detectedLang,
                    targetLang,
                    originalText,
                    translatedText,
                    isSaved,
                    LocalDateTime.now()
            );
            TranslationHistory saved = historyRepository.save(history);
            historyId = saved.getId();
        }

        // Update User Profile stats
        updateUserStats(originalText);

        // Generate alternatives & pronunciation guidance
        alternatives = generateAlternatives(originalText, translatedText, detectedLang, targetLang);

        return new TranslationResponse(
                reqSourceLang,
                targetLang,
                detectedLang,
                originalText,
                translatedText,
                historyId,
                isSaved,
                pronunciation,
                translatedPronunciation,
                alternatives
        );
    }

    private void updateUserStats(String text) {
        try {
            Optional<UserProfile> profileOpt = userProfileRepository.findFirstByOrderByIdAsc();
            if (profileOpt.isPresent()) {
                UserProfile profile = profileOpt.get();
                profile.setTotalTranslationsCount(profile.getTotalTranslationsCount() + 1);
                int words = text.split("\\s+").length;
                profile.setWordsTranslatedCount(profile.getWordsTranslatedCount() + words);
                userProfileRepository.save(profile);
            }
        } catch (Exception ignored) {
        }
    }

    private static class GtxResult {
        String translatedText;
        String detectedLang;
        String pronunciation;
    }

    private GtxResult translateViaGtx(String text, String sl, String tl) throws Exception {
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String urlStr = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sl +
                "&tl=" + tl + "&dt=t&dt=bd&dt=rm&q=" + encodedText;

        URI uri = URI.create(urlStr);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JsonNode root = objectMapper.readTree(response.toString());
                GtxResult result = new GtxResult();

                if (root.isArray() && root.size() > 0) {
                    JsonNode sentences = root.get(0);
                    StringBuilder sb = new StringBuilder();
                    if (sentences.isArray()) {
                        for (JsonNode sentence : sentences) {
                            if (sentence.isArray() && sentence.size() > 0 && !sentence.get(0).isNull()) {
                                String chunk = sentence.get(0).asText();
                                if (!chunk.isBlank()) {
                                    sb.append(chunk);
                                }
                            }
                        }
                    }
                    result.translatedText = sb.toString();

                    if (root.size() > 2 && root.get(2) != null && !root.get(2).isNull()) {
                        result.detectedLang = root.get(2).asText();
                    }
                }
                return result;
            }
        }
        return null;
    }

    private String translateViaMyMemory(String text, String sl, String tl) throws Exception {
        String langpair = sl + "|" + tl;
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String urlStr = "https://api.mymemory.translated.net/get?q=" + encodedText + "&langpair=" + langpair;

        URI uri = URI.create(urlStr);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setConnectTimeout(4000);
        conn.setReadTimeout(4000);

        if (conn.getResponseCode() == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                JsonNode root = objectMapper.readTree(response.toString());
                if (root.has("responseData") && root.get("responseData").has("translatedText")) {
                    return root.get("responseData").get("translatedText").asText();
                }
            }
        }
        return null;
    }

    public String detectLanguageLocally(String text) {
        if (text == null || text.isBlank()) return "en";
        String lower = text.toLowerCase();

        // Check Cyrillic (Russian)
        for (char c : lower.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CYRILLIC) {
                return "ru";
            }
        }

        // Check Azerbaijani specific chars
        if (lower.contains("ə") || lower.contains("xəbər") || lower.contains("necə") ||
                lower.contains("salam") || lower.contains("yaxşı") || lower.contains("hər") ||
                lower.contains("üçün") || lower.contains("daha") || lower.contains("tərcümə")) {
            return "az";
        }

        // Check Turkish specific
        if (lower.contains("merhaba") || lower.contains("nasılsın") || lower.contains("güzel") ||
                lower.contains("teşekkür") || lower.contains("evet") || lower.contains("hayır")) {
            return "tr";
        }

        // Check Spanish
        if (lower.contains("hola") || lower.contains("gracias") || lower.contains("buenos") ||
                lower.contains("por favor") || lower.contains("amigo") || lower.contains("¿")) {
            return "es";
        }

        // Check German
        if (lower.contains("guten tag") || lower.contains("danke") || lower.contains("bitte") ||
                lower.contains("auf wiedersehen") || lower.contains("ja") || lower.contains("nein")) {
            return "de";
        }

        // Check French
        if (lower.contains("bonjour") || lower.contains("merci") || lower.contains("oui") ||
                lower.contains("s'il vous plaît") || lower.contains("au revoir")) {
            return "fr";
        }

        return "en";
    }

    private String translateViaDictionary(String text, String sl, String tl) {
        String clean = text.trim().toLowerCase();
        Map<String, String> langDict = dictionary.get(sl + "_" + tl);
        if (langDict != null && langDict.containsKey(clean)) {
            return langDict.get(clean);
        }

        // Try word by word
        if (langDict != null) {
            String[] words = text.split(" ");
            StringBuilder sb = new StringBuilder();
            boolean anyTranslated = false;
            for (String w : words) {
                String punctuation = "";
                String pure = w;
                if (!w.isEmpty() && !Character.isLetterOrDigit(w.charAt(w.length() - 1))) {
                    punctuation = String.valueOf(w.charAt(w.length() - 1));
                    pure = w.substring(0, w.length() - 1);
                }
                String trans = langDict.get(pure.toLowerCase());
                if (trans != null) {
                    sb.append(trans).append(punctuation).append(" ");
                    anyTranslated = true;
                } else {
                    sb.append(w).append(" ");
                }
            }
            if (anyTranslated) {
                return sb.toString().trim();
            }
        }

        return text;
    }

    private List<String> generateAlternatives(String original, String translated, String sl, String tl) {
        List<String> list = new ArrayList<>();
        String origLower = original.trim().toLowerCase();

        if (origLower.equals("salam") && tl.equals("en")) {
            list.add("Hi");
            list.add("Hey there");
            list.add("Greetings");
        } else if (origLower.equals("hello") && tl.equals("az")) {
            list.add("Xoş gördük");
            list.add("Hər vaxtınız xeyir");
        } else if (origLower.contains("necəsiniz") && tl.equals("en")) {
            list.add("How are you doing?");
            list.add("How is it going?");
        } else if (origLower.contains("təşəkkür") && tl.equals("en")) {
            list.add("Thanks a lot");
            list.add("Many thanks");
        }
        return list;
    }

    private void addEntry(String sl, String tl, String src, String trg) {
        String key = sl + "_" + tl;
        dictionary.computeIfAbsent(key, k -> new HashMap<>()).put(src.toLowerCase(), trg);
    }

    private void initDictionary() {
        // AZ -> EN
        addEntry("az", "en", "salam", "Hello");
        addEntry("az", "en", "sabahınız xeyir", "Good morning");
        addEntry("az", "en", "hər vaxtınız xeyir", "Good day");
        addEntry("az", "en", "axşamınız xeyir", "Good evening");
        addEntry("az", "en", "gecəniz xeyrə", "Good night");
        addEntry("az", "en", "necəsiniz?", "How are you?");
        addEntry("az", "en", "necəsiniz", "How are you");
        addEntry("az", "en", "yaxşıyam", "I am fine");
        addEntry("az", "en", "çox sağ olun", "Thank you very much");
        addEntry("az", "en", "təşəkkür edirəm", "Thank you");
        addEntry("az", "en", "buyurun", "You are welcome");
        addEntry("az", "en", "bəli", "Yes");
        addEntry("az", "en", "xeyr", "No");
        addEntry("az", "en", "bağışlayın", "Excuse me / Sorry");
        addEntry("az", "en", "xudahafiz", "Goodbye");
        addEntry("az", "en", "sağ olun", "Thank you / Goodbye");
        addEntry("az", "en", "adınız nədir?", "What is your name?");
        addEntry("az", "en", "mənim adım", "My name is");
        addEntry("az", "en", "haradansınız?", "Where are you from?");
        addEntry("az", "en", "tərcümə", "Translation");
        addEntry("az", "en", "dil", "Language");
        addEntry("az", "en", "səs", "Voice");
        addEntry("az", "en", "yadda saxlanılmışlar", "Saved");
        addEntry("az", "en", "tarixçə", "History");
        addEntry("az", "en", "hesab", "Account");
        addEntry("az", "en", "tənzimləmələr", "Settings");
        addEntry("az", "en", "kitab", "Book");
        addEntry("az", "en", "kompüter", "Computer");
        addEntry("az", "en", "proqramlaşdırma", "Programming");
        addEntry("az", "en", "dünya", "World");
        addEntry("az", "en", "həyat", "Life");
        addEntry("az", "en", "sevgi", "Love");
        addEntry("az", "en", "dost", "Friend");

        // EN -> AZ
        addEntry("en", "az", "hello", "Salam");
        addEntry("en", "az", "hi", "Salam");
        addEntry("en", "az", "good morning", "Sabahınız xeyir");
        addEntry("en", "az", "good afternoon", "Hər vaxtınız xeyir");
        addEntry("en", "az", "good evening", "Axşamınız xeyir");
        addEntry("en", "az", "good night", "Gecəniz xeyrə");
        addEntry("en", "az", "how are you?", "Necəsiniz?");
        addEntry("en", "az", "how are you", "Necəsiniz");
        addEntry("en", "az", "i am fine", "Yaxşıyam");
        addEntry("en", "az", "thank you", "Təşəkkür edirəm");
        addEntry("en", "az", "thanks", "Çox sağ ol");
        addEntry("en", "az", "you are welcome", "Buyurun");
        addEntry("en", "az", "yes", "Bəli");
        addEntry("en", "az", "no", "Xeyr");
        addEntry("en", "az", "please", "Zəhmət olmasa");
        addEntry("en", "az", "sorry", "Bağışlayın");
        addEntry("en", "az", "goodbye", "Xudahafiz");
        addEntry("en", "az", "what is your name?", "Adınız nədir?");
        addEntry("en", "az", "my name is", "Mənim adım");
        addEntry("en", "az", "where are you from?", "Haradansınız?");
        addEntry("en", "az", "translation", "Tərcümə");
        addEntry("en", "az", "language", "Dil");
        addEntry("en", "az", "voice", "Səs");
        addEntry("en", "az", "saved", "Yadda saxlanılanlar");
        addEntry("en", "az", "history", "Tarixçə");
        addEntry("en", "az", "account", "Hesab");
        addEntry("en", "az", "settings", "Tənzimləmələr");
        addEntry("en", "az", "computer", "Kompüter");
        addEntry("en", "az", "programming", "Proqramlaşdırma");
        addEntry("en", "az", "friend", "Dost");

        // AZ -> TR
        addEntry("az", "tr", "salam", "Merhaba");
        addEntry("az", "tr", "sabahınız xeyir", "Günaydın");
        addEntry("az", "tr", "necəsiniz?", "Nasılsınız?");
        addEntry("az", "tr", "yaxşıyam", "İyiyim");
        addEntry("az", "tr", "çox sağ olun", "Teşekkür ederim");
        addEntry("az", "tr", "bağışlayın", "Özür dilerim");
        addEntry("az", "tr", "xudahafiz", "Hoşça kalın");
        addEntry("az", "tr", "bəli", "Evet");
        addEntry("az", "tr", "xeyr", "Hayır");

        // TR -> AZ
        addEntry("tr", "az", "merhaba", "Salam");
        addEntry("tr", "az", "günaydın", "Sabahınız xeyir");
        addEntry("tr", "az", "nasılsınız?", "Necəsiniz?");
        addEntry("tr", "az", "iyiyim", "Yaxşıyam");
        addEntry("tr", "az", "teşekkür ederim", "Çox sağ olun");
        addEntry("tr", "az", "evet", "Bəli");
        addEntry("tr", "az", "hayır", "Xeyr");
        addEntry("tr", "az", "hoşça kalın", "Xudahafiz");

        // EN -> TR
        addEntry("en", "tr", "hello", "Merhaba");
        addEntry("en", "tr", "good morning", "Günaydın");
        addEntry("en", "tr", "how are you?", "Nasılsınız?");
        addEntry("en", "tr", "thank you", "Teşekkür ederim");
        addEntry("en", "tr", "goodbye", "Hoşça kalın");

        // TR -> EN
        addEntry("tr", "en", "merhaba", "Hello");
        addEntry("tr", "en", "günaydın", "Good morning");
        addEntry("tr", "en", "nasılsınız?", "How are you?");
        addEntry("tr", "en", "teşekkür ederim", "Thank you");
        addEntry("tr", "en", "hoşça kalın", "Goodbye");

        // EN -> RU
        addEntry("en", "ru", "hello", "Привет");
        addEntry("en", "ru", "good morning", "Доброе утро");
        addEntry("en", "ru", "how are you?", "Как дела?");
        addEntry("en", "ru", "thank you", "Спасибо");
        addEntry("en", "ru", "goodbye", "До свидания");

        // RU -> EN
        addEntry("ru", "en", "привет", "Hello");
        addEntry("ru", "en", "доброе утро", "Good morning");
        addEntry("ru", "en", "как дела?", "How are you?");
        addEntry("ru", "en", "спасибо", "Thank you");
        addEntry("ru", "en", "до свидания", "Goodbye");

        // AZ -> RU
        addEntry("az", "ru", "salam", "Привет");
        addEntry("az", "ru", "sabahınız xeyir", "Доброе утро");
        addEntry("az", "ru", "necəsiniz?", "Как дела?");
        addEntry("az", "ru", "çox sağ olun", "Большое спасибо");

        // RU -> AZ
        addEntry("ru", "az", "привет", "Salam");
        addEntry("ru", "az", "доброе утро", "Sabahınız xeyir");
        addEntry("ru", "az", "как дела?", "Necəsiniz?");
        addEntry("ru", "az", "спасибо", "Çox sağ olun");

        // EN -> ES
        addEntry("en", "es", "hello", "Hola");
        addEntry("en", "es", "good morning", "Buenos días");
        addEntry("en", "es", "how are you?", "¿Cómo estás?");
        addEntry("en", "es", "thank you", "Gracias");
        addEntry("en", "es", "goodbye", "Adiós");

        // ES -> EN
        addEntry("es", "en", "hola", "Hello");
        addEntry("es", "en", "buenos días", "Good morning");
        addEntry("es", "en", "¿cómo estás?", "How are you?");
        addEntry("es", "en", "gracias", "Thank you");
        addEntry("es", "en", "adiós", "Goodbye");
    }
}
