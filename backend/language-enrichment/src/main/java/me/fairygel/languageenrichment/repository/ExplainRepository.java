package me.fairygel.languageenrichment.repository;

import me.fairygel.languageenrichment.entity.Definition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ExplainRepository extends JpaRepository<Definition, UUID> {
    @Query("""
            SELECT d FROM Definition d
            JOIN FETCH d.word w
            WHERE w.text = :wordText AND w.language = :wordLanguage
            """)
    List<Definition> findActive(String wordText, String wordLanguage);
}
