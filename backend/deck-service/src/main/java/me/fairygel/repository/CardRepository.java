package me.fairygel.repository;

import me.fairygel.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
    Optional<Card> findByIdAndDeck_UserId(UUID id, UUID deckUserId);
    List<Card> findAllByDeckIdAndDeck_UserId(UUID deckId, UUID deckUserId);
    long deleteByIdAndDeck_UserId(UUID id, UUID deckUserId);
}
