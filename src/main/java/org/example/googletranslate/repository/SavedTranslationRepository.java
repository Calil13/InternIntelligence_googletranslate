package org.example.googletranslate.repository;

import org.example.googletranslate.model.SavedTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedTranslationRepository extends JpaRepository<SavedTranslation, Long> {

    List<SavedTranslation> findAllByOrderBySavedAtDesc();

    Optional<SavedTranslation> findByOriginalTextAndSourceLanguageAndTargetLanguage(
            String originalText, String sourceLanguage, String targetLanguage);

    boolean existsByOriginalTextAndSourceLanguageAndTargetLanguage(
            String originalText, String sourceLanguage, String targetLanguage);

    @Query("SELECT s FROM SavedTranslation s WHERE " +
           "(LOWER(s.originalText) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(s.translatedText) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:sourceLang IS NULL OR s.sourceLanguage = :sourceLang) AND " +
           "(:targetLang IS NULL OR s.targetLanguage = :targetLang) " +
           "ORDER BY s.savedAt DESC")
    List<SavedTranslation> searchSaved(
            @Param("query") String query,
            @Param("sourceLang") String sourceLang,
            @Param("targetLang") String targetLang);
}
