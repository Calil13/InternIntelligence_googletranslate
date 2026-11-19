package org.example.googletranslate.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_settings")
public class AppSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String theme; // "light", "dark", "system"

    private boolean autoDetect;

    private boolean instantTranslate;

    private double speechSpeed; // 0.8, 1.0, 1.2

    private double voicePitch; // 0.8, 1.0, 1.2

    private boolean autoPlayAudio;

    private boolean historyEnabled;

    @Column(length = 10)
    private String interfaceLanguage; // "az", "en", "tr", "ru"

    public AppSettings() {
    }

    public AppSettings(String theme, boolean autoDetect, boolean instantTranslate,
                       double speechSpeed, double voicePitch, boolean autoPlayAudio,
                       boolean historyEnabled, String interfaceLanguage) {
        this.theme = theme;
        this.autoDetect = autoDetect;
        this.instantTranslate = instantTranslate;
        this.speechSpeed = speechSpeed;
        this.voicePitch = voicePitch;
        this.autoPlayAudio = autoPlayAudio;
        this.historyEnabled = historyEnabled;
        this.interfaceLanguage = interfaceLanguage;
    }

    public static AppSettings defaultSettings() {
        return new AppSettings("light", true, true, 1.0, 1.0, false, true, "az");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isAutoDetect() {
        return autoDetect;
    }

    public void setAutoDetect(boolean autoDetect) {
        this.autoDetect = autoDetect;
    }

    public boolean isInstantTranslate() {
        return instantTranslate;
    }

    public void setInstantTranslate(boolean instantTranslate) {
        this.instantTranslate = instantTranslate;
    }

    public double getSpeechSpeed() {
        return speechSpeed;
    }

    public void setSpeechSpeed(double speechSpeed) {
        this.speechSpeed = speechSpeed;
    }

    public double getVoicePitch() {
        return voicePitch;
    }

    public void setVoicePitch(double voicePitch) {
        this.voicePitch = voicePitch;
    }

    public boolean isAutoPlayAudio() {
        return autoPlayAudio;
    }

    public void setAutoPlayAudio(boolean autoPlayAudio) {
        this.autoPlayAudio = autoPlayAudio;
    }

    public boolean isHistoryEnabled() {
        return historyEnabled;
    }

    public void setHistoryEnabled(boolean historyEnabled) {
        this.historyEnabled = historyEnabled;
    }

    public String getInterfaceLanguage() {
        return interfaceLanguage;
    }

    public void setInterfaceLanguage(String interfaceLanguage) {
        this.interfaceLanguage = interfaceLanguage;
    }
}
