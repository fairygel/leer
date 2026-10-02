package me.fairygel.repository;

import me.fairygel.entity.Card;
import me.fairygel.entity.Deck;
import me.fairygel.enums.CardStatus;
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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CardRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer container = new PostgreSQLContainer("postgres:17.10-alpine");

    @Autowired
    CardRepository cardRepository;

    @Autowired
    DeckRepository deckRepository;

    Deck deck;
    Deck strangerDeck;

    @BeforeEach
    void setUp() {
        deck = new Deck();
        deck.setName("Test Deck");
        deck.setUserId(UUID.randomUUID());
        deck.setSourceLang(Language.RU);
        deck.setTargetLang(Language.EN);
        deckRepository.saveAndFlush(deck);

        strangerDeck = new Deck();
        strangerDeck.setName("Stranger Deck");
        strangerDeck.setUserId(UUID.randomUUID());
        strangerDeck.setSourceLang(Language.EN);
        strangerDeck.setTargetLang(Language.RU);
        deckRepository.saveAndFlush(strangerDeck);
    }

    private Card persistCard(Deck deck, CardStatus status, String question) {
        Card card = new Card();
        card.setDeck(deck);
        card.setQuestion(question);
        card.setAnswer("Answer for " + question);
        card.setStatus(status);
        return cardRepository.saveAndFlush(card);
    }

    @Test
    void findCardsByStatus_filtersByStatusDeckAndUser() {
        persistCard(deck, CardStatus.NEW, "q1");
        persistCard(deck, CardStatus.NEW, "q2");
        persistCard(deck, CardStatus.KNOWN, "q3");
        persistCard(strangerDeck, CardStatus.NEW, "stranger q");

        List<Card> newCards = cardRepository.findCardsByStatus(
                deck.getUserId(), deck.getId(), CardStatus.NEW);

        assertThat(newCards).hasSize(2)
                .allMatch(c -> c.getStatus() == CardStatus.NEW)
                .extracting(Card::getQuestion)
                .containsExactlyInAnyOrder("q1", "q2");

        List<Card> knownCards = cardRepository.findCardsByStatus(
                deck.getUserId(), deck.getId(), CardStatus.KNOWN);

        assertThat(knownCards).hasSize(1)
                .extracting(Card::getQuestion)
                .containsExactly("q3");
    }

    @Test
    void findCardsByStatus_returnsEmpty_forStrangersDeck() {
        persistCard(deck, CardStatus.NEW, "q1");

        List<Card> result = cardRepository.findCardsByStatus(
                strangerDeck.getUserId(), deck.getId(), CardStatus.NEW);

        assertThat(result).isEmpty();
    }

    @Test
    void findByDeck_UserIdAndId_respectsOwner() {
        Card card = persistCard(deck, CardStatus.NEW, "q1");

        assertThat(cardRepository.findByDeck_UserIdAndId(deck.getUserId(), card.getId())).isPresent();
        assertThat(cardRepository.findByDeck_UserIdAndId(UUID.randomUUID(), card.getId())).isEmpty();
    }

    @Test
    void findAllByDeck_UserIdAndDeckId_returnsOnlyOwnDeckCards() {
        persistCard(deck, CardStatus.NEW, "own q1");
        persistCard(deck, CardStatus.KNOWN, "own q2");
        persistCard(strangerDeck, CardStatus.NEW, "stranger q");

        List<Card> result = cardRepository.findAllByDeck_UserIdAndDeckId(
                deck.getUserId(), deck.getId());

        assertThat(result).hasSize(2)
                .extracting(Card::getQuestion)
                .containsExactlyInAnyOrder("own q1", "own q2");
    }

    @Test
    void countByDeckId_groupsByStatus() {
        persistCard(deck, CardStatus.NEW, "q1");
        persistCard(deck, CardStatus.NEW, "q2");
        persistCard(deck, CardStatus.KNOWN, "q3");
        persistCard(strangerDeck, CardStatus.NEW, "stranger q");

        Map<CardStatus, Long> stats = cardRepository.countByDeckId(deck.getId(), deck.getUserId())
                .stream()
                .collect(Collectors.toMap(
                        CardRepository.CardStatusCount::getStatus,
                        CardRepository.CardStatusCount::getCnt));

        assertThat(stats)
                .containsEntry(CardStatus.NEW, 2L)
                .containsEntry(CardStatus.KNOWN, 1L)
                .doesNotContainKey(CardStatus.UNKNOWN);
    }

    @Test
    void deleteByDeck_UserIdAndId_deletesOnlyOwn() {
        Card card = persistCard(deck, CardStatus.NEW, "q1");

        assertThat(cardRepository.deleteByDeck_UserIdAndId(UUID.randomUUID(), card.getId())).isZero();
        assertThat(cardRepository.findByDeck_UserIdAndId(deck.getUserId(), card.getId())).isPresent();

        assertThat(cardRepository.deleteByDeck_UserIdAndId(deck.getUserId(), card.getId())).isOne();
        assertThat(cardRepository.findByDeck_UserIdAndId(deck.getUserId(), card.getId())).isEmpty();
    }

    @Test
    void resetLearningForAll_setsAllToNew_andTouchesOnlyOwnDeck() {
        persistCard(deck, CardStatus.KNOWN, "own q1");
        persistCard(deck, CardStatus.UNKNOWN, "own q2");
        Card strangerCard = persistCard(strangerDeck, CardStatus.KNOWN, "stranger q");

        cardRepository.resetLearningForAll(deck.getUserId(), deck.getId(), CardStatus.NEW);

        List<Card> reset = cardRepository.findCardsByStatus(
                deck.getUserId(), deck.getId(), CardStatus.NEW);
        assertThat(reset).hasSize(2);

        List<Card> leftovers = cardRepository.findCardsByStatus(
                deck.getUserId(), deck.getId(), CardStatus.KNOWN);
        assertThat(leftovers).isEmpty();

        assertThat(cardRepository.findByDeck_UserIdAndId(
                strangerDeck.getUserId(), strangerCard.getId()))
                .isPresent()
                .get()
                .extracting(Card::getStatus)
                .isEqualTo(CardStatus.KNOWN);
    }
}
