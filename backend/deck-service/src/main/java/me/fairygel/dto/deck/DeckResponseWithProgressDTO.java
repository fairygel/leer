package me.fairygel.dto.deck;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DeckResponseWithProgressDTO(
        UUID id,
        String name,
        String description,
        OffsetDateTime lastViewedAt,
        OffsetDateTime updatedAt,
        OffsetDateTime createdAt,
        DeckLearnProgressDTO learnProgress
) { }
