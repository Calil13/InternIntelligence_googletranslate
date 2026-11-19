package org.example.googletranslate.model;

public class Language {
    private String code;
    private String name;
    private String nativeName;
    private String voiceCode;
    private String flag;

    public Language() {
    }

    public Language(String code, String name, String nativeName, String voiceCode, String flag) {
        this.code = code;
        this.name = name;
        this.nativeName = nativeName;
        this.voiceCode = voiceCode;
        this.flag = flag;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    public String getVoiceCode() {
        return voiceCode;
    }

    public void setVoiceCode(String voiceCode) {
        this.voiceCode = voiceCode;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
