package me.fairygel.dto.deck;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.fairygel.enums.Language;

public record CreateDeckRequestDTO(
        @NotBlank(message = "Name cannot be blank")
        @Size(max = 255, message = "Name length must not exceed 255 characters")
        String name,

        @Size(max = 1000, message = "Description length must not exceed 1000 characters")
        String description,

        @NotNull(message = "Source language is required")
        Language sourceLang,

        @NotNull(message = "Target language is required")
        Language targetLang
) {}