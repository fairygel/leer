package me.fairygel.dto.deck;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DeckResponseDTO(
        UUID id,
        String name,
        String description,
        UUID userId,
        OffsetDateTime lastViewedAt,
        OffsetDateTime updatedAt,
        OffsetDateTime createdAt
) {}