package me.fairygel.dto.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCardRequestDTO(
        @NotNull(message = "Deck ID is required")
        UUID deckId,

        @NotBlank(message = "Question cannot be blank")
        @Size(max = 255, message = "Question length must not exceed 255 characters")
        String question,

        @NotBlank(message = "Answer cannot be blank")
        @Size(max = 255, message = "Answer length must not exceed 255 characters")
        String answer,

        @Size(max = 1000, message = "Note length must not exceed 1000 characters")
        String note
) {}