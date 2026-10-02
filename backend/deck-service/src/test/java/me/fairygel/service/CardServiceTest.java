package me.fairygel.service;

import jakarta.persistence.EntityNotFoundException;
import me.fairygel.dto.card.CardResponseDTO;
import me.fairygel.dto.card.CreateCardRequestDTO;
import me.fairygel.dto.card.UpdateCardStatusRequestDTO;
import me.fairygel.entity.Card;
import me.fairygel.entity.Deck;
import me.fairygel.enums.CardStatus;
import me.fairygel.mapper.CardMapper;
import me.fairygel.repository.CardRepository;
import me.fairygel.repository.DeckRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    CardRepository cardRepository;
    @Mock
    DeckRepository deckRepository;
    @Mock
    CardMapper cardMapper;

    @InjectMocks
    CardService cardService;

    private final UUID userId = UUID.randomUUID();
    private final UUID deckId = UUID.randomUUID();
    private final UUID cardId = UUID.randomUUID();

    @Test
    void create_throws_andDoesNotSave_whenStrangersDeck() {
        var request = new CreateCardRequestDTO(deckId, "q", "a", null);
        when(deckRepository.findByUserIdAndId(userId, deckId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> cardService.create(userId, request));

        verify(cardRepository, never()).save(any());
    }

    @Test
    void create_savesWithResolvedDeck_whenOwnDeck() {
        var request = new CreateCardRequestDTO(deckId, "q", "a", null);
        Deck deck = new Deck();
        deck.setUserId(userId);
        when(deckRepository.findByUserIdAndId(userId, deckId)).thenReturn(Optional.of(deck));
        Card entity = new Card();
        when(cardMapper.toEntity(request)).thenReturn(entity);
        Card saved = new Card();
        when(cardRepository.save(entity)).thenReturn(saved);
        when(cardMapper.toResponse(saved)).thenReturn(
                new CardResponseDTO(null, deckId, "q", "a", null, CardStatus.NEW, null, null, null));

        CardResponseDTO result = cardService.create(userId, request);

        assertThat(entity.getDeck()).isSameAs(deck);
        assertThat(result.question()).isEqualTo("q");
        verify(cardRepository).save(entity);
    }

    @Test
    void delete_throws_andDoesNotDelete_whenStrangersCard() {
        when(cardRepository.deleteByDeck_UserIdAndId(userId, cardId)).thenReturn(0L);

        assertThrows(EntityNotFoundException.class,
                () -> cardService.deleteByIdAndUserId(userId, cardId));
    }

    @Test
    void findById_throws_whenStrangersCard() {
        when(cardRepository.findByDeck_UserIdAndId(userId, cardId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> cardService.findByIdAndUserId(userId, cardId));
    }

    @Test
    void setKnown_setsKnown_whenTrue() {
        Card card = new Card();
        card.setStatus(CardStatus.NEW);
        when(cardRepository.findByDeck_UserIdAndId(userId, cardId)).thenReturn(Optional.of(card));
        when(cardMapper.toResponse(card)).thenReturn(
                new CardResponseDTO(cardId, deckId, "q", "a", null, CardStatus.KNOWN, null, null, null));

        CardResponseDTO result = cardService.setKnown(userId, cardId, new UpdateCardStatusRequestDTO(true));

        assertThat(card.getStatus()).isEqualTo(CardStatus.KNOWN);
        assertThat(result.status()).isEqualTo(CardStatus.KNOWN);
    }

    @Test
    void setKnown_setsUnknown_whenFalse() {
        Card card = new Card();
        card.setStatus(CardStatus.KNOWN);
        when(cardRepository.findByDeck_UserIdAndId(userId, cardId)).thenReturn(Optional.of(card));
        when(cardMapper.toResponse(card)).thenReturn(
                new CardResponseDTO(cardId, deckId, "q", "a", null, CardStatus.UNKNOWN, null, null, null));

        CardResponseDTO result = cardService.setKnown(userId, cardId, new UpdateCardStatusRequestDTO(false));

        assertThat(card.getStatus()).isEqualTo(CardStatus.UNKNOWN);
        assertThat(result.status()).isEqualTo(CardStatus.UNKNOWN);
    }
}
