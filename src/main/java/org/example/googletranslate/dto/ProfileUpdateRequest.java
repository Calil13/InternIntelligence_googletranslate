package org.example.googletranslate.dto;

public class ProfileUpdateRequest {
    private String fullName;
    private String email;
    private String bio;
    private String nativeLanguage;
    private String preferredTargetLanguage;
    private String avatarUrl;

    public ProfileUpdateRequest() {
    }

    public ProfileUpdateRequest(String fullName, String email, String bio, String nativeLanguage,
                                String preferredTargetLanguage, String avatarUrl) {
        this.fullName = fullName;
        this.email = email;
        this.bio = bio;
        this.nativeLanguage = nativeLanguage;
        this.preferredTargetLanguage = preferredTargetLanguage;
        this.avatarUrl = avatarUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getNativeLanguage() {
        return nativeLanguage;
    }

    public void setNativeLanguage(String nativeLanguage) {
        this.nativeLanguage = nativeLanguage;
    }

    public String getPreferredTargetLanguage() {
        return preferredTargetLanguage;
    }

    public void setPreferredTargetLanguage(String preferredTargetLanguage) {
        this.preferredTargetLanguage = preferredTargetLanguage;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
