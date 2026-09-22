package me.fairygel.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.fairygel.dto.deck.CreateDeckRequestDTO;
import me.fairygel.dto.deck.DeckResponseDTO;
import me.fairygel.dto.deck.UpdateDeckRequestDTO;
import me.fairygel.entity.Deck;
import me.fairygel.mapper.DeckMapper;
import me.fairygel.repository.DeckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeckService {
    private final DeckRepository deckRepository;
    private final DeckMapper deckMapper;

    @Transactional(readOnly = true)
    public List<DeckResponseDTO> findAllByUserId(UUID userId) {
        List<Deck> rawDecks = deckRepository.findAllByUserId(userId);

        return deckMapper.toResponseList(rawDecks);
    }

    @Transactional(readOnly = true)
    public DeckResponseDTO findByIdAndUserId(UUID deckId, UUID userId) {
        Deck rawDeck = deckRepository.findByIdAndUserId(deckId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Deck not found with id: " + deckId));

        return deckMapper.toResponse(rawDeck);
    }

    @Transactional
    public void deleteByIdAndUserId(UUID deckId, UUID userId) {
        boolean isDeleted = deckRepository.deleteByIdAndUserId(deckId, userId) > 0;

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
    public DeckResponseDTO update(UUID deckId, UUID userId, UpdateDeckRequestDTO deckUpdateDTO) {
        Deck rawDeck = deckRepository.findByIdAndUserId(deckId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Deck not found with id: " + deckId));

        deckMapper.update(deckUpdateDTO, rawDeck);

        return deckMapper.toResponse(rawDeck);
    }
}