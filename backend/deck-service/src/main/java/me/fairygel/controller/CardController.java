package me.fairygel.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.fairygel.dto.card.CardResponseDTO;
import me.fairygel.dto.card.CreateCardRequestDTO;
import me.fairygel.dto.card.UpdateCardRequestDTO;
import me.fairygel.dto.card.UpdateCardStatusRequestDTO;
import me.fairygel.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/deck/{deckId}")
    public List<CardResponseDTO> getCardsByDeckId(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID deckId
    ) {
        return cardService.findAllByDeckIdAndUserId(userId, deckId);
    }

    @GetMapping("/{cardId}")
    public CardResponseDTO getCard(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID cardId
    ) {
        return cardService.findByIdAndUserId(userId, cardId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponseDTO createCard(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateCardRequestDTO cardRequestDTO
    ) {
        return cardService.create(userId, cardRequestDTO);
    }

    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID cardId
    ) {
        cardService.deleteByIdAndUserId(userId, cardId);
    }

    @PatchMapping("/{cardId}")
    public CardResponseDTO updateCard(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID cardId,
            @Valid @RequestBody UpdateCardRequestDTO cardRequestDTO
    ) {
        return cardService.update(userId, cardId, cardRequestDTO);
    }

    @PatchMapping("/{cardId}/set-known")
    public CardResponseDTO setKnown(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID cardId,
            @Valid @RequestBody UpdateCardStatusRequestDTO cardRequestDTO
    ) {
        return cardService.setKnown(userId, cardId, cardRequestDTO);
    }
}