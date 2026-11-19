package org.example.googletranslate.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    private String avatarUrl;

    @Column(length = 500)
    private String bio;

    private String nativeLanguage;

    private String preferredTargetLanguage;

    private LocalDate memberSince;

    private long totalTranslationsCount;

    private long wordsTranslatedCount;

    public UserProfile() {
    }

    public UserProfile(String fullName, String email, String avatarUrl, String bio,
                       String nativeLanguage, String preferredTargetLanguage, LocalDate memberSince) {
        this.fullName = fullName;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.nativeLanguage = nativeLanguage;
        this.preferredTargetLanguage = preferredTargetLanguage;
        this.memberSince = memberSince;
        this.totalTranslationsCount = 0;
        this.wordsTranslatedCount = 0;
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

    public long getTotalTranslationsCount() {
        return totalTranslationsCount;
    }

    public void setTotalTranslationsCount(long totalTranslationsCount) {
        this.totalTranslationsCount = totalTranslationsCount;
    }

    public long getWordsTranslatedCount() {
        return wordsTranslatedCount;
    }

    public void setWordsTranslatedCount(long wordsTranslatedCount) {
        this.wordsTranslatedCount = wordsTranslatedCount;
    }
}
