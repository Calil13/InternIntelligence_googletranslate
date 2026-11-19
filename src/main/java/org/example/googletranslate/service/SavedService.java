package org.example.googletranslate.service;

import org.example.googletranslate.dto.SavedRequest;
import org.example.googletranslate.model.SavedTranslation;
import org.example.googletranslate.repository.SavedTranslationRepository;
import org.example.googletranslate.repository.TranslationHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SavedService {

    private final SavedTranslationRepository savedRepository;
    private final TranslationHistoryRepository historyRepository;

    public SavedService(SavedTranslationRepository savedRepository,
                        TranslationHistoryRepository historyRepository) {
        this.savedRepository = savedRepository;
        this.historyRepository = historyRepository;
    }

    public List<SavedTranslation> getSaved(String query, String sourceLang, String targetLang) {
        if ((query != null && !query.trim().isEmpty()) || sourceLang != null || targetLang != null) {
            String q = query != null ? query.trim() : "";
            String sl = (sourceLang != null && !sourceLang.isBlank()) ? sourceLang : null;
            String tl = (targetLang != null && !targetLang.isBlank()) ? targetLang : null;
            return savedRepository.searchSaved(q, sl, tl);
        }
        return savedRepository.findAllByOrderBySavedAtDesc();
    }

    @Transactional
    public SavedTranslation saveTranslation(SavedRequest req) {
        Optional<SavedTranslation> existing = savedRepository.findByOriginalTextAndSourceLanguageAndTargetLanguage(
                req.getOriginalText(), req.getSourceLanguage(), req.getTargetLanguage()
        );

        if (existing.isPresent()) {
            SavedTranslation item = existing.get();
            item.setTranslatedText(req.getTranslatedText());
            item.setNote(req.getNote());
            item.setSavedAt(LocalDateTime.now());
            return savedRepository.save(item);
        }

        SavedTranslation newItem = new SavedTranslation(
                req.getSourceLanguage(),
                req.getTargetLanguage(),
                req.getOriginalText(),
                req.getTranslatedText(),
                req.getNote(),
                LocalDateTime.now()
        );
        return savedRepository.save(newItem);
    }

    @Transactional
    public void removeSaved(Long id) {
        savedRepository.findById(id).ifPresent(item -> {
            // Also mark corresponding history item as not saved if exists
            savedRepository.deleteById(id);
        });
    }

    @Transactional
    public void unsaveByTextAndLanguages(String originalText, String sl, String tl) {
        savedRepository.findByOriginalTextAndSourceLanguageAndTargetLanguage(originalText, sl, tl)
                .ifPresent(savedRepository::delete);
    }

    public void clearAll() {
        savedRepository.deleteAll();
    }

    public long getSavedCount() {
        return savedRepository.count();
    }

    public String exportToCsv() {
        List<SavedTranslation> list = savedRepository.findAllByOrderBySavedAtDesc();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Source Language,Target Language,Original Text,Translated Text,Note,Saved At\n");
        for (SavedTranslation s : list) {
            sb.append(s.getId()).append(",")
                    .append(escapeCsv(s.getSourceLanguage())).append(",")
                    .append(escapeCsv(s.getTargetLanguage())).append(",")
                    .append(escapeCsv(s.getOriginalText())).append(",")
                    .append(escapeCsv(s.getTranslatedText())).append(",")
                    .append(escapeCsv(s.getNote() != null ? s.getNote() : "")).append(",")
                    .append(s.getSavedAt()).append("\n");
        }
        return sb.toString();
    }

    private String escapeCsv(String val) {
        if (val == null) return "\"\"";
        return "\"" + val.replace("\"", "\"\"") + "\"";
    }
}
