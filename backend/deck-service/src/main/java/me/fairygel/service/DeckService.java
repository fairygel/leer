package me.fairygel.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.fairygel.dto.card.CardResponseDTO;
import me.fairygel.dto.deck.CreateDeckRequestDTO;
import me.fairygel.dto.deck.DeckResponseDTO;
import me.fairygel.dto.deck.UpdateDeckRequestDTO;
import me.fairygel.entity.Card;
import me.fairygel.entity.Deck;
import me.fairygel.enums.CardStatus;
import me.fairygel.mapper.CardMapper;
import me.fairygel.mapper.DeckMapper;
import me.fairygel.repository.CardRepository;
import me.fairygel.repository.DeckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeckService {
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final DeckMapper deckMapper;
    private final CardMapper cardMapper;

    @Transactional(readOnly = true)
    public List<DeckResponseDTO> findAllByUserId(UUID userId) {
        List<Deck> rawDecks = deckRepository.findAllByUserId(userId);

        return deckMapper.toResponseList(rawDecks);
    }

    @Transactional(readOnly = true)
    public List<CardResponseDTO> getLearningDeck(UUID userId, UUID deckId) {
        List<Card> cardsToLearn = cardRepository.findCardsByStatus(userId, deckId, CardStatus.NEW);

        if (cardsToLearn.isEmpty()) {
            cardsToLearn = cardRepository.findCardsByStatus(userId, deckId, CardStatus.UNKNOWN);
        }

        return cardMapper.toResponseList(cardsToLearn);
    }


    @Transactional(readOnly = true)
    public DeckResponseDTO findByIdAndUserId(UUID userId, UUID deckId) {
        Deck rawDeck = deckRepository.findByUserIdAndId(userId, deckId)
                .orElseThrow(() -> new EntityNotFoundException("Deck not found with id: " + deckId));

        return deckMapper.toResponse(rawDeck);
    }

    @Transactional
    public void deleteByIdAndUserId(UUID userId, UUID deckId) {
        boolean isDeleted = deckRepository.deleteByUserIdAndId(userId, deckId) > 0;

        if (!isDeleted) {
            throw new EntityNotFoundException("Deck not found with id: " + deckId);
        }
    }

    @Transactional
    public DeckResponseDTO create(UUID userId, CreateDeckRequestDTO deckCreateDTO) {
        Deck deckEntity = deckMapper.toEntity(deckCreateDTO);
        deckEntity.setUserId(userId);

        Deck savedDeckEntity = deckRepository.save(deckEntity);

        return deckMapper.toResponse(savedDeckEntity);
    }

    @Transactional
    public DeckResponseDTO update(UUID userId, UUID deckId, UpdateDeckRequestDTO deckUpdateDTO) {
        Deck rawDeck = deckRepository.findByUserIdAndId(userId, deckId)
                .orElseThrow(() -> new EntityNotFoundException("Deck not found with id: " + deckId));

        deckMapper.update(deckUpdateDTO, rawDeck);

        return deckMapper.toResponse(rawDeck);
    }

    @Transactional
    public void resetLearning(UUID userId, UUID deckId) {
        if (!deckRepository.existsByUserIdAndId(userId, deckId)) {
            throw new EntityNotFoundException("Deck not found with id: " + deckId);
        }

        cardRepository.resetLearningForAll(userId, deckId, CardStatus.NEW);
    }
}