package me.fairygel.dto.deck;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.fairygel.enums.Language;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DeckResponseDTO(
        UUID id,
        String name,
        String description,
        Language sourceLang,
        Language targetLang,
        OffsetDateTime lastViewedAt,
        OffsetDateTime updatedAt,
        OffsetDateTime createdAt
) {
    @JsonProperty("translation")
    public boolean translation() {
        return sourceLang != null && targetLang != null && !sourceLang.equals(targetLang);
    }
}