package me.fairygel.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.fairygel.dto.card.CardResponseDTO;
import me.fairygel.dto.card.CreateCardRequestDTO;
import me.fairygel.dto.card.UpdateCardRequestDTO;
import me.fairygel.dto.card.UpdateCardStatusRequestDTO;
import me.fairygel.entity.Card;
import me.fairygel.entity.Deck;
import me.fairygel.enums.CardStatus;
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
    public List<CardResponseDTO> findAllByDeckIdAndUserId(UUID userId, UUID deckId) {
        List<Card> rawCards = cardRepository.findAllByDeck_UserIdAndDeckId(userId, deckId);

        return cardMapper.toResponseList(rawCards);
    }

    @Transactional(readOnly = true)
    public CardResponseDTO findByIdAndUserId(UUID userId, UUID cardId) {
        Card rawCard = findCardOrThrow(userId, cardId);

        return cardMapper.toResponse(rawCard);
    }

    @Transactional
    public void deleteByIdAndUserId(UUID userId, UUID cardId) {
        boolean isDeleted = cardRepository.deleteByDeck_UserIdAndId(userId, cardId) > 0;

        if (!isDeleted) {
            throw new EntityNotFoundException("Card not found with id: " + cardId);
        }
    }

    @Transactional
    public CardResponseDTO create(UUID userId, CreateCardRequestDTO cardCreateDTO) {
        UUID deckId = cardCreateDTO.deckId();

        Deck deck = deckRepository.findByUserIdAndId(userId, deckId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Deck not found with id: " + deckId
                ));

        Card cardEntity = cardMapper.toEntity(cardCreateDTO);

        cardEntity.setDeck(deck);

        Card savedCardEntity = cardRepository.save(cardEntity);

        return cardMapper.toResponse(savedCardEntity);
    }

    @Transactional
    public CardResponseDTO update(UUID userId, UUID cardId, UpdateCardRequestDTO cardUpdateDTO) {
        Card rawCard = findCardOrThrow(userId, cardId);
        cardMapper.update(cardUpdateDTO, rawCard);

        return cardMapper.toResponse(rawCard);
    }

    @Transactional
    public CardResponseDTO setKnown(UUID userId, UUID cardId, UpdateCardStatusRequestDTO cardRequestDTO) {
        Card rawCard = findCardOrThrow(userId, cardId);
        CardStatus cardStatus = cardRequestDTO.isKnown() ? CardStatus.KNOWN : CardStatus.UNKNOWN;

        rawCard.setStatus(cardStatus);

        return cardMapper.toResponse(rawCard);
    }

    private Card findCardOrThrow(UUID userId, UUID cardId) {
        return cardRepository.findByDeck_UserIdAndId(userId, cardId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Card not found with id: " + cardId
                ));
    }
}