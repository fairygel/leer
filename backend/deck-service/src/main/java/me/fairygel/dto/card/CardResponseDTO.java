package me.fairygel.dto.card;

import me.fairygel.enums.CardStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CardResponseDTO(
        UUID id,
        UUID deckId,
        String question,
        String answer,
        String note,
        CardStatus status,
        OffsetDateTime lastViewedAt,
        OffsetDateTime updatedAt,
        OffsetDateTime createdAt
) {}