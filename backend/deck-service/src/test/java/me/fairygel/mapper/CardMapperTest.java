package me.fairygel.mapper;

import me.fairygel.dto.card.CardResponseDTO;
import me.fairygel.dto.card.CreateCardRequestDTO;
import me.fairygel.dto.card.UpdateCardRequestDTO;
import me.fairygel.entity.Card;
import me.fairygel.entity.Deck;
import me.fairygel.enums.CardStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CardMapperTest {

    private final CardMapper mapper = new CardMapperImpl();

    @Test
    void toEntity_mapsFields_andLeavesDeckAndStatusUntouched() {
        var dto = new CreateCardRequestDTO(UUID.randomUUID(), "q", "a", "note");

        Card entity = mapper.toEntity(dto);

        assertThat(entity.getQuestion()).isEqualTo("q");
        assertThat(entity.getAnswer()).isEqualTo("a");
        assertThat(entity.getNote()).isEqualTo("note");

        assertThat(entity.getDeck()).isNull();

        assertThat(entity.getStatus()).isEqualTo(CardStatus.NEW);
        assertThat(entity.getId()).isNull();
    }

    @Test
    void update_ignoresNulls_doesNotOverwrite() {
        Card card = new Card();
        card.setQuestion("Old q");
        card.setAnswer("Old a");
        card.setNote("Old note");
        card.setStatus(CardStatus.KNOWN);

        mapper.update(new UpdateCardRequestDTO("New q", null, null), card);

        assertThat(card.getQuestion()).isEqualTo("New q");
        assertThat(card.getAnswer()).isEqualTo("Old a");
        assertThat(card.getNote()).isEqualTo("Old note");

        assertThat(card.getStatus()).isEqualTo(CardStatus.KNOWN);
    }

    @Test
    void toResponse_mapsDeckId() {
        Deck deck = new Deck();
        UUID deckId = UUID.randomUUID();
        deck.setId(deckId);
        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setDeck(deck);
        card.setQuestion("q");
        card.setAnswer("a");
        card.setStatus(CardStatus.NEW);

        var response = mapper.toResponse(card);

        assertThat(response.deckId()).isEqualTo(deckId);
        assertThat(response.question()).isEqualTo("q");
        assertThat(response.status()).isEqualTo(CardStatus.NEW);
    }

    @Test
    void toResponseList_mapsEach() {
        Card c1 = new Card();
        c1.setQuestion("q1");
        c1.setAnswer("a1");
        Card c2 = new Card();
        c2.setQuestion("q2");
        c2.setAnswer("a2");

        var result = mapper.toResponseList(List.of(c1, c2));

        assertThat(result).hasSize(2)
                .extracting(CardResponseDTO::question)
                .containsExactly("q1", "q2");
    }
}
