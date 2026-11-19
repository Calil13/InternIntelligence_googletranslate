package org.example.googletranslate.service;

import org.example.googletranslate.model.SavedTranslation;
import org.example.googletranslate.model.TranslationHistory;
import org.example.googletranslate.repository.SavedTranslationRepository;
import org.example.googletranslate.repository.TranslationHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HistoryService {

    private final TranslationHistoryRepository historyRepository;
    private final SavedTranslationRepository savedRepository;

    public HistoryService(TranslationHistoryRepository historyRepository,
                          SavedTranslationRepository savedRepository) {
        this.historyRepository = historyRepository;
        this.savedRepository = savedRepository;
    }

    public List<TranslationHistory> getHistory(String query) {
        if (query != null && !query.trim().isEmpty()) {
            return historyRepository.searchHistory(query.trim());
        }
        return historyRepository.findAllByOrderByTimestampDesc();
    }

    public Optional<TranslationHistory> findById(Long id) {
        return historyRepository.findById(id);
    }

    public void deleteHistoryItem(Long id) {
        historyRepository.deleteById(id);
    }

    public void clearHistory() {
        historyRepository.deleteAll();
    }

    @Transactional
    public boolean toggleSave(Long historyId) {
        Optional<TranslationHistory> historyOpt = historyRepository.findById(historyId);
        if (historyOpt.isEmpty()) {
            return false;
        }

        TranslationHistory history = historyOpt.get();
        boolean newSavedState = !history.isSaved();
        history.setSaved(newSavedState);
        historyRepository.save(history);

        if (newSavedState) {
            // Add to saved if not already exists
            if (!savedRepository.existsByOriginalTextAndSourceLanguageAndTargetLanguage(
                    history.getOriginalText(), history.getSourceLanguage(), history.getTargetLanguage())) {
                SavedTranslation savedItem = new SavedTranslation(
                        history.getSourceLanguage(),
                        history.getTargetLanguage(),
                        history.getOriginalText(),
                        history.getTranslatedText(),
                        null,
                        LocalDateTime.now()
                );
                savedRepository.save(savedItem);
            }
        } else {
            // Remove from saved
            savedRepository.findByOriginalTextAndSourceLanguageAndTargetLanguage(
                    history.getOriginalText(), history.getSourceLanguage(), history.getTargetLanguage()
            ).ifPresent(savedRepository::delete);
        }

        return newSavedState;
    }
}
