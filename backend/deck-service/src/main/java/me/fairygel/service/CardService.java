package me.fairygel.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.fairygel.dto.card.CardResponseDTO;
import me.fairygel.dto.card.CreateCardRequestDTO;
import me.fairygel.dto.card.UpdateCardRequestDTO;
import me.fairygel.entity.Card;
import me.fairygel.entity.Deck;
import me.fairygel.mapper.CardMapper;
import me.fairygel.repository.CardRepository;
import me.fairygel.repository.DeckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final CardMapper cardMapper;

    @Transactional(readOnly = true)
    public List<CardResponseDTO> findAllByDeckIdAndUserId(UUID deckId, UUID userId) {
        List<Card> rawCards = cardRepository.findAllByDeckIdAndDeck_UserId(deckId, userId);

        return cardMapper.toResponseList(rawCards);
    }

    @Transactional(readOnly = true)
    public CardResponseDTO findByIdAndUserId(UUID cardId, UUID userId) {
        Card rawCard = cardRepository.findByIdAndDeck_UserId(cardId, userId).orElseThrow(() -> new EntityNotFoundException(
                "Card not found with id: " + cardId
        ));

        return cardMapper.toResponse(rawCard);
    }

    @Transactional
    public void deleteByIdAndUserId(UUID cardId, UUID userId) {
        boolean isDeleted = cardRepository.deleteByIdAndDeck_UserId(cardId, userId) > 0;

        if (!isDeleted) {
            throw new EntityNotFoundException("Card not found with id: " + cardId);
        }
    }

    @Transactional
    public CardResponseDTO create(UUID userId, CreateCardRequestDTO cardCreateDTO) {
        UUID deckId = cardCreateDTO.deckId();

        Deck deck = deckRepository.findByIdAndUserId(deckId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Deck not found with id: " + deckId
                ));

        Card cardEntity = cardMapper.toEntity(cardCreateDTO);

        cardEntity.setDeck(deck);

        Card savedCardEntity = cardRepository.save(cardEntity);

        return cardMapper.toResponse(savedCardEntity);
    }

    @Transactional
    public CardResponseDTO update(UUID cardId, UUID userId, UpdateCardRequestDTO cardUpdateDTO) {
        Card rawCard = cardRepository.findByIdAndDeck_UserId(cardId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Card not found with id: " + cardId
                ));

        cardMapper.update(cardUpdateDTO, rawCard);

        return cardMapper.toResponse(rawCard);
    }
}