package me.fairygel.repository;

import me.fairygel.entity.Deck;
import me.fairygel.enums.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DeckRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer container = new PostgreSQLContainer("postgres:17.10-alpine");

    @Autowired
    DeckRepository deckRepository;

    Deck deck;

    @BeforeEach
    void createDeck() {
        deck = new Deck();

        deck.setName("Test Deck");
        deck.setUserId(UUID.randomUUID());
        deck.setDescription("Test Description");
        deck.setSourceLang(Language.RU);
        deck.setTargetLang(Language.EN);

        deckRepository.save(deck);
    }

    @Test
    void existsByUserIdAndId_canAndCannotFind() {
        boolean exists = deckRepository.existsByUserIdAndId(deck.getUserId(), deck.getId());

        assertThat(exists).isTrue();

        boolean notExists = deckRepository.existsByUserIdAndId(UUID.randomUUID(), UUID.randomUUID());

        assertThat(notExists).isFalse();
    }

    @Test
    void findByUserIdAndId_canFind() {
        Optional<Deck> exists = deckRepository.findByUserIdAndId(deck.getUserId(), deck.getId());

        assertThat(exists).isPresent();

        Deck existingDeck = exists.get();

        assertThat(existingDeck.getName()).isEqualTo("Test Deck");
        assertThat(existingDeck.getDescription()).isEqualTo("Test Description");

        Optional<Deck> notExists = deckRepository.findByUserIdAndId(existingDeck.getUserId(), UUID.randomUUID());

        assertThat(notExists).isNotPresent();
    }

    @Test
    void findAllByUserId_returnsOnlyOwnDecks() {
        UUID owner = deck.getUserId();

        Deck own2 = new Deck();
        own2.setName("Second Deck");
        own2.setUserId(owner);
        own2.setSourceLang(Language.RU);
        own2.setTargetLang(Language.EN);
        deckRepository.save(own2);

        Deck strangerDeck = new Deck();
        strangerDeck.setName("Stranger Deck");
        strangerDeck.setUserId(UUID.randomUUID());
        strangerDeck.setSourceLang(Language.EN);
        strangerDeck.setTargetLang(Language.RU);
        deckRepository.save(strangerDeck);

        List<Deck> result = deckRepository.findAllByUserId(owner);

        assertThat(result)
                .hasSize(2)
                .allMatch(d -> d.getUserId().equals(owner));
        assertThat(result).extracting(Deck::getName)
                .containsExactlyInAnyOrder("Test Deck", "Second Deck");
    }

    @Test
    void deleteByUserIdAndId_deletesOnlyOwn() {
        assertThat(deckRepository.deleteByUserIdAndId(UUID.randomUUID(), deck.getId())).isZero();
        assertThat(deckRepository.existsByUserIdAndId(deck.getUserId(), deck.getId())).isTrue();

        assertThat(deckRepository.deleteByUserIdAndId(deck.getUserId(), deck.getId())).isOne();
        assertThat(deckRepository.existsByUserIdAndId(deck.getUserId(), deck.getId())).isFalse();
    }

}
