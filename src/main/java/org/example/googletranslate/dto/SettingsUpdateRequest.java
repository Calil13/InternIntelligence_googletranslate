package org.example.googletranslate.dto;

public class SettingsUpdateRequest {
    private String theme;
    private Boolean autoDetect;
    private Boolean instantTranslate;
    private Double speechSpeed;
    private Double voicePitch;
    private Boolean autoPlayAudio;
    private Boolean historyEnabled;
    private String interfaceLanguage;

    public SettingsUpdateRequest() {
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Boolean getAutoDetect() {
        return autoDetect;
    }

    public void setAutoDetect(Boolean autoDetect) {
        this.autoDetect = autoDetect;
    }

    public Boolean getInstantTranslate() {
        return instantTranslate;
    }

    public void setInstantTranslate(Boolean instantTranslate) {
        this.instantTranslate = instantTranslate;
    }

    public Double getSpeechSpeed() {
        return speechSpeed;
    }

    public void setSpeechSpeed(Double speechSpeed) {
        this.speechSpeed = speechSpeed;
    }

    public Double getVoicePitch() {
        return voicePitch;
    }

    public void setVoicePitch(Double voicePitch) {
        this.voicePitch = voicePitch;
    }

    public Boolean getAutoPlayAudio() {
        return autoPlayAudio;
    }

    public void setAutoPlayAudio(Boolean autoPlayAudio) {
        this.autoPlayAudio = autoPlayAudio;
    }

    public Boolean getHistoryEnabled() {
        return historyEnabled;
    }

    public void setHistoryEnabled(Boolean historyEnabled) {
        this.historyEnabled = historyEnabled;
    }

    public String getInterfaceLanguage() {
        return interfaceLanguage;
    }

    public void setInterfaceLanguage(String interfaceLanguage) {
        this.interfaceLanguage = interfaceLanguage;
    }
}
