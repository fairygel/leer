package me.fairygel.dto.card;

import jakarta.validation.constraints.Size;
import me.fairygel.enums.CardStatus;

public record UpdateCardRequestDTO(
        @Size(min = 1, max = 255, message = "Question must be between 1 and 255 characters")
        String question,

        @Size(min = 1, max = 255, message = "Answer must be between 1 and 255 characters")
        String answer,

        CardStatus status
) {}