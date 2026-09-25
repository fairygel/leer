package me.fairygel.dto.card;

import jakarta.validation.constraints.Size;

public record UpdateCardRequestDTO(
        @Size(min = 1, max = 255, message = "Question must be between 1 and 255 characters")
        String question,

        @Size(min = 1, max = 255, message = "Answer must be between 1 and 255 characters")
        String answer,

        @Size(max = 1000, message = "Note length must not exceed 1000 characters")
        String note
) {}