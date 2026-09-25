package me.fairygel.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.fairygel.dto.card.CardResponseDTO;
import me.fairygel.dto.deck.CreateDeckRequestDTO;
import me.fairygel.dto.deck.DeckResponseDTO;
import me.fairygel.dto.deck.DeckResponseWithProgressDTO;
import me.fairygel.dto.deck.UpdateDeckRequestDTO;
import me.fairygel.service.DeckService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/decks")
@RequiredArgsConstructor
public class DeckController {
    private final DeckService deckService;

    @GetMapping
    public List<DeckResponseDTO> getDecks(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return deckService.findAllByUserId(userId);
    }

    @GetMapping("/{deckId}/learn")
    public List<CardResponseDTO> getLearningDeck(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID deckId) {
        return deckService.getLearningDeck(userId, deckId);
    }

    @GetMapping("/{deckId}")
    public DeckResponseWithProgressDTO getDeck(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID deckId
    ) {
        return deckService.findByIdAndUserId(userId, deckId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeckResponseDTO createDeck(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateDeckRequestDTO deckRequestDTO
    ) {
        return deckService.create(userId, deckRequestDTO);
    }

    @DeleteMapping("/{deckId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDeck(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID deckId
    ) {
        deckService.deleteByIdAndUserId(userId, deckId);
    }

    @PatchMapping("/{deckId}")
    public DeckResponseDTO updateDeck(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID deckId,
            @Valid @RequestBody UpdateDeckRequestDTO deckRequestDTO
    ) {
        return deckService.update(userId, deckId, deckRequestDTO);
    }

    @PostMapping("/{deckId}/reset-learning")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetLearning(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID deckId
    ) {
        deckService.resetLearning(userId, deckId);
    }
}
