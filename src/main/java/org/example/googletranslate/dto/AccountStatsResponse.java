package org.example.googletranslate.dto;

import java.time.LocalDate;

public class AccountStatsResponse {
    private Long id;
    private String fullName;
    private String email;
    private String avatarUrl;
    private String bio;
    private String nativeLanguage;
    private String preferredTargetLanguage;
    private LocalDate memberSince;
    private long totalTranslations;
    private long totalWordsTranslated;
    private long totalSavedCount;
    private String mostFrequentPair;
    private int supportedLanguagesCount;

    public AccountStatsResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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

    public LocalDate getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(LocalDate memberSince) {
        this.memberSince = memberSince;
    }

    public long getTotalTranslations() {
        return totalTranslations;
    }

    public void setTotalTranslations(long totalTranslations) {
        this.totalTranslations = totalTranslations;
    }

    public long getTotalWordsTranslated() {
        return totalWordsTranslated;
    }

    public void setTotalWordsTranslated(long totalWordsTranslated) {
        this.totalWordsTranslated = totalWordsTranslated;
    }

    public long getTotalSavedCount() {
        return totalSavedCount;
    }

    public void setTotalSavedCount(long totalSavedCount) {
        this.totalSavedCount = totalSavedCount;
    }

    public String getMostFrequentPair() {
        return mostFrequentPair;
    }

    public void setMostFrequentPair(String mostFrequentPair) {
        this.mostFrequentPair = mostFrequentPair;
    }

    public int getSupportedLanguagesCount() {
        return supportedLanguagesCount;
    }

    public void setSupportedLanguagesCount(int supportedLanguagesCount) {
        this.supportedLanguagesCount = supportedLanguagesCount;
    }
}
