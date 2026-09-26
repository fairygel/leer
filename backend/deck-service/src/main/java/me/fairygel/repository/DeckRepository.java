package me.fairygel.repository;

import me.fairygel.entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeckRepository extends JpaRepository<Deck, UUID> {

    Optional<Deck> findByUserIdAndId(UUID userId, UUID id);

    List<Deck> findAllByUserId(UUID userId);

    long deleteByUserIdAndId(UUID userId, UUID id);

    boolean existsByUserIdAndId(UUID userId, UUID id);
}
