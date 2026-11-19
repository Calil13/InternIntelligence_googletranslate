package org.example.googletranslate.service;

import org.example.googletranslate.model.Language;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LanguageService {

    private final Map<String, Language> languageMap = new LinkedHashMap<>();

    public LanguageService() {
        register(new Language("az", "Azərbaycan", "Azərbaycanca", "az-AZ", "🇦🇿"));
        register(new Language("en", "İngilis", "English", "en-US", "🇺🇸"));
        register(new Language("tr", "Türk", "Türkçe", "tr-TR", "🇹🇷"));
        register(new Language("ru", "Rus", "Русский", "ru-RU", "🇷🇺"));
        register(new Language("es", "İspan", "Español", "es-ES", "🇪🇸"));
        register(new Language("de", "Alman", "Deutsch", "de-DE", "🇩🇪"));
        register(new Language("fr", "Fransız", "Français", "fr-FR", "🇫🇷"));
        register(new Language("it", "İtalyan", "Italiano", "it-IT", "🇮🇹"));
        register(new Language("ar", "Ərəb", "العربية", "ar-SA", "🇸🇦"));
        register(new Language("zh", "Çin", "中文", "zh-CN", "🇨🇳"));
    }

    private void register(Language lang) {
        languageMap.put(lang.getCode().toLowerCase(), lang);
    }

    public List<Language> getAllLanguages() {
        return new ArrayList<>(languageMap.values());
    }

    public Optional<Language> findByCode(String code) {
        if (code == null) return Optional.empty();
        return Optional.ofNullable(languageMap.get(code.toLowerCase()));
    }

    public boolean isSupported(String code) {
        if (code == null) return false;
        return "auto".equalsIgnoreCase(code) || languageMap.containsKey(code.toLowerCase());
    }

    public String getVoiceCode(String langCode) {
        Language l = languageMap.get(langCode != null ? langCode.toLowerCase() : "");
        return l != null ? l.getVoiceCode() : "en-US";
    }

    public String getLanguageName(String langCode) {
        Language l = languageMap.get(langCode != null ? langCode.toLowerCase() : "");
        return l != null ? l.getName() : langCode;
    }
}
