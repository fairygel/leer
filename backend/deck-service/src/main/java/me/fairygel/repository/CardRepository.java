package me.fairygel.repository;

import me.fairygel.entity.Card;
import me.fairygel.enums.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    interface CardStatusCount {
        CardStatus getStatus();
        long getCnt();
    }

    @Query("""
                SELECT c.status AS status, COUNT(c) AS cnt
                FROM Card c
                WHERE c.deck.id = :deckId AND c.deck.userId = :userId
                GROUP BY c.status
            """)
    List<CardStatusCount> countByDeckId(
            @Param("deckId") UUID deckId,
            @Param("userId") UUID userId);
    @Query("""
                SELECT c FROM Card c WHERE c.status = :status AND c.deck.id = :deckId AND c.deck.userId = :userId
            """)
    List<Card> findCardsByStatus(UUID userId, UUID deckId, CardStatus status);

    Optional<Card> findByDeck_UserIdAndId(UUID userId, UUID id);

    List<Card> findAllByDeck_UserIdAndDeckId(UUID userId, UUID deckId);

    long deleteByDeck_UserIdAndId(UUID userId, UUID id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                UPDATE Card c SET c.status = :status WHERE c.deck.id = :deckId AND c.deck.userId = :userId
            """)
    void resetLearningForAll(UUID userId, UUID deckId, CardStatus status);
}
