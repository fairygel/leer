package me.fairygel.languageenrichment.repository;

import me.fairygel.languageenrichment.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TranslateRepository extends JpaRepository<Translation, UUID> {
   @Query("""
            SELECT t FROM Translation t
            JOIN FETCH t.word w
            WHERE w.text = :wordText AND w.language = :sourceLanguage AND t.language = :targetLanguage
            """)
   List<Translation> findActive(String wordText, String sourceLanguage, String targetLanguage);
}
