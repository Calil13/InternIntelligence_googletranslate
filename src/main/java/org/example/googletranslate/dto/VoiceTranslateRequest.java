package org.example.googletranslate.dto;

public class VoiceTranslateRequest {
    private String sourceLanguage;
    private String targetLanguage;
    private String transcript;
    private boolean autoPlay;

    public VoiceTranslateRequest() {
    }

    public VoiceTranslateRequest(String sourceLanguage, String targetLanguage, String transcript, boolean autoPlay) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.transcript = transcript;
        this.autoPlay = autoPlay;
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

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }
}
