package me.fairygel.dto.deck;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import me.fairygel.enums.Language;

public record UpdateDeckRequestDTO(
        @Pattern(regexp = "(?s).*\\S.*", message = "Name cannot be blank")
        @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
        String name,

        @Size(max = 1000, message = "Description length must not exceed 1000 characters")
        String description,

        Language sourceLang,

        Language targetLang
) {}