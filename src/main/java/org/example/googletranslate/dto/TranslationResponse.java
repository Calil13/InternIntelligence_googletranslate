package org.example.googletranslate.dto;

import java.util.List;

public class TranslationResponse {
    private String sourceLanguage;
    private String targetLanguage;
    private String detectedLanguage;
    private String originalText;
    private String translatedText;
    private Long historyId;
    private boolean isSaved;
    private String pronunciation;
    private String translatedPronunciation;
    private List<String> alternatives;

    public TranslationResponse() {
    }

    public TranslationResponse(String sourceLanguage, String targetLanguage, String detectedLanguage,
                               String originalText, String translatedText, Long historyId, boolean isSaved,
                               String pronunciation, String translatedPronunciation, List<String> alternatives) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.detectedLanguage = detectedLanguage;
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.historyId = historyId;
        this.isSaved = isSaved;
        this.pronunciation = pronunciation;
        this.translatedPronunciation = translatedPronunciation;
        this.alternatives = alternatives;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getDetectedLanguage() {
        return detectedLanguage;
    }

    public void setDetectedLanguage(String detectedLanguage) {
        this.detectedLanguage = detectedLanguage;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getTranslatedPronunciation() {
        return translatedPronunciation;
    }

    public void setTranslatedPronunciation(String translatedPronunciation) {
        this.translatedPronunciation = translatedPronunciation;
    }

    public List<String> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<String> alternatives) {
        this.alternatives = alternatives;
    }
}
