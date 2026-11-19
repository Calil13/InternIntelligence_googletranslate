package org.example.googletranslate.dto;

public class SavedRequest {
    private String sourceLanguage;
    private String targetLanguage;
    private String originalText;
    private String translatedText;
    private String note;

    public SavedRequest() {
    }

    public SavedRequest(String sourceLanguage, String targetLanguage, String originalText, String translatedText, String note) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.note = note;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
