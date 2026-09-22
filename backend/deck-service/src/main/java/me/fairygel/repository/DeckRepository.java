package me.fairygel.repository;

import me.fairygel.entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeckRepository extends JpaRepository<Deck, UUID> {
    Optional<Deck> findByIdAndUserId(UUID id, UUID userId);
    List<Deck> findAllByUserId(UUID userId);
    long deleteByIdAndUserId(UUID id, UUID userId);
}
