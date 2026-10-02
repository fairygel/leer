package me.fairygel.service;

import jakarta.persistence.EntityNotFoundException;
import me.fairygel.dto.deck.CreateDeckRequestDTO;
import me.fairygel.dto.deck.DeckResponseDTO;
import me.fairygel.entity.Card;
import me.fairygel.entity.Deck;
import me.fairygel.enums.CardStatus;
import me.fairygel.enums.Language;
import me.fairygel.mapper.CardMapper;
import me.fairygel.mapper.DeckMapper;
import me.fairygel.repository.CardRepository;
import me.fairygel.repository.DeckRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeckServiceTest {

    @Mock
    DeckRepository deckRepository;
    @Mock
    CardRepository cardRepository;
    @Mock
    DeckMapper deckMapper;
    @Mock
    CardMapper cardMapper;

    @InjectMocks
    DeckService deckService;

    private final UUID userId = UUID.randomUUID();
    private final UUID deckId = UUID.randomUUID();

    @Test
    void getLearningDeck_returnsNew_withoutQueryingUnknown() {
        List<Card> newCards = List.of(new Card());
        when(cardRepository.findCardsByStatus(userId, deckId, CardStatus.NEW)).thenReturn(newCards);
        when(cardMapper.toResponseList(newCards)).thenReturn(List.of());

        deckService.getLearningDeck(userId, deckId);

        verify(cardRepository).findCardsByStatus(userId, deckId, CardStatus.NEW);
        verify(cardRepository, never()).findCardsByStatus(userId, deckId, CardStatus.UNKNOWN);
    }

    @Test
    void getLearningDeck_fallsBackToUnknown_whenNoNew() {
        List<Card> unknownCards = List.of(new Card());
        when(cardRepository.findCardsByStatus(userId, deckId, CardStatus.NEW)).thenReturn(List.of());
        when(cardRepository.findCardsByStatus(userId, deckId, CardStatus.UNKNOWN)).thenReturn(unknownCards);
        when(cardMapper.toResponseList(unknownCards)).thenReturn(List.of());

        deckService.getLearningDeck(userId, deckId);

        verify(cardRepository).findCardsByStatus(userId, deckId, CardStatus.UNKNOWN);
    }

    @Test
    void findByIdAndUserId_throws_whenStrangersDeck() {
        when(deckRepository.findByUserIdAndId(userId, deckId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> deckService.findByIdAndUserId(userId, deckId));

        verify(cardRepository, never()).countByDeckId(any(), any());
    }

    @Test
    void create_setsUserIdFromHeader() {
        var request = new CreateDeckRequestDTO("German A1", "desc", Language.EN, Language.RU);
        Deck entity = new Deck();
        when(deckMapper.toEntity(request)).thenReturn(entity);
        Deck saved = new Deck();
        saved.setUserId(userId);
        when(deckRepository.save(entity)).thenReturn(saved);
        when(deckMapper.toResponse(saved)).thenReturn(
                new DeckResponseDTO(null, "German A1", "desc", Language.EN, Language.RU, null, null, null));

        DeckResponseDTO result = deckService.create(userId, request);

        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(result.name()).isEqualTo("German A1");
        verify(deckRepository).save(entity);
    }

    @Test
    void resetLearning_throws_andDoesNotReset_whenStrangersDeck() {
        when(deckRepository.existsByUserIdAndId(userId, deckId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> deckService.resetLearning(userId, deckId));

        verify(cardRepository, never()).resetLearningForAll(any(), any(), any());
    }

    @Test
    void resetLearning_resets_whenOwnDeck() {
        when(deckRepository.existsByUserIdAndId(userId, deckId)).thenReturn(true);

        deckService.resetLearning(userId, deckId);

        verify(cardRepository).resetLearningForAll(userId, deckId, CardStatus.NEW);
    }

    @Test
    void delete_throws_whenStrangersDeck() {
        when(deckRepository.deleteByUserIdAndId(userId, deckId)).thenReturn(0L);

        assertThrows(EntityNotFoundException.class,
                () -> deckService.deleteByIdAndUserId(userId, deckId));
    }
}
