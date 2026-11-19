package org.example.googletranslate.repository;

import org.example.googletranslate.model.TranslationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslationHistoryRepository extends JpaRepository<TranslationHistory, Long> {

    List<TranslationHistory> findAllByOrderByTimestampDesc();

    @Query("SELECT h FROM TranslationHistory h WHERE " +
           "LOWER(h.originalText) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(h.translatedText) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY h.timestamp DESC")
    List<TranslationHistory> searchHistory(@Param("query") String query);

    @Query("SELECT h.sourceLanguage, h.targetLanguage, COUNT(h) FROM TranslationHistory h " +
           "GROUP BY h.sourceLanguage, h.targetLanguage " +
           "ORDER BY COUNT(h) DESC")
    List<Object[]> findMostFrequentLanguagePairs();
}
