package org.example.googletranslate.service;

import org.example.googletranslate.dto.AccountStatsResponse;
import org.example.googletranslate.dto.ProfileUpdateRequest;
import org.example.googletranslate.model.UserProfile;
import org.example.googletranslate.repository.SavedTranslationRepository;
import org.example.googletranslate.repository.TranslationHistoryRepository;
import org.example.googletranslate.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AccountService {

    private final UserProfileRepository userProfileRepository;
    private final TranslationHistoryRepository historyRepository;
    private final SavedTranslationRepository savedRepository;
    private final LanguageService languageService;

    public AccountService(UserProfileRepository userProfileRepository,
                          TranslationHistoryRepository historyRepository,
                          SavedTranslationRepository savedRepository,
                          LanguageService languageService) {
        this.userProfileRepository = userProfileRepository;
        this.historyRepository = historyRepository;
        this.savedRepository = savedRepository;
        this.languageService = languageService;
    }

    public UserProfile getOrCreateProfile() {
        return userProfileRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    UserProfile profile = new UserProfile(
                            "Celil Aliyev",
                            "celil@example.com",
                            "https://lh3.googleusercontent.com/a/default-user=s96-c",
                            "Google Translate istifadəçisi. Dilləri öyrənməyi və ünsiyyət qurmağı sevirəm.",
                            "az",
                            "en",
                            LocalDate.now().minusMonths(6)
                    );
                    profile.setTotalTranslationsCount(historyRepository.count());
                    return userProfileRepository.save(profile);
                });
    }

    public AccountStatsResponse getAccountStats() {
        UserProfile profile = getOrCreateProfile();
        long historyCount = historyRepository.count();
        long savedCount = savedRepository.count();

        // Calculate most frequent pair
        String mostFrequentPair = "az → en";
        try {
            List<Object[]> pairs = historyRepository.findMostFrequentLanguagePairs();
            if (pairs != null && !pairs.isEmpty()) {
                Object[] top = pairs.get(0);
                String sl = (String) top[0];
                String tl = (String) top[1];
                mostFrequentPair = sl.toUpperCase() + " → " + tl.toUpperCase();
            }
        } catch (Exception ignored) {
        }

        AccountStatsResponse resp = new AccountStatsResponse();
        resp.setId(profile.getId());
        resp.setFullName(profile.getFullName());
        resp.setEmail(profile.getEmail());
        resp.setAvatarUrl(profile.getAvatarUrl());
        resp.setBio(profile.getBio());
        resp.setNativeLanguage(profile.getNativeLanguage());
        resp.setPreferredTargetLanguage(profile.getPreferredTargetLanguage());
        resp.setMemberSince(profile.getMemberSince());
        resp.setTotalTranslations(Math.max(profile.getTotalTranslationsCount(), historyCount));
        resp.setTotalWordsTranslated(profile.getWordsTranslatedCount());
        resp.setTotalSavedCount(savedCount);
        resp.setMostFrequentPair(mostFrequentPair);
        resp.setSupportedLanguagesCount(languageService.getAllLanguages().size());

        return resp;
    }

    public UserProfile updateProfile(ProfileUpdateRequest req) {
        UserProfile profile = getOrCreateProfile();
        if (req.getFullName() != null && !req.getFullName().isBlank()) {
            profile.setFullName(req.getFullName().trim());
        }
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            profile.setEmail(req.getEmail().trim());
        }
        if (req.getBio() != null) {
            profile.setBio(req.getBio().trim());
        }
        if (req.getNativeLanguage() != null && !req.getNativeLanguage().isBlank()) {
            profile.setNativeLanguage(req.getNativeLanguage().trim());
        }
        if (req.getPreferredTargetLanguage() != null && !req.getPreferredTargetLanguage().isBlank()) {
            profile.setPreferredTargetLanguage(req.getPreferredTargetLanguage().trim());
        }
        if (req.getAvatarUrl() != null && !req.getAvatarUrl().isBlank()) {
            profile.setAvatarUrl(req.getAvatarUrl().trim());
        }
        return userProfileRepository.save(profile);
    }

    public void resetStats() {
        UserProfile profile = getOrCreateProfile();
        profile.setTotalTranslationsCount(0);
        profile.setWordsTranslatedCount(0);
        userProfileRepository.save(profile);
    }
}
