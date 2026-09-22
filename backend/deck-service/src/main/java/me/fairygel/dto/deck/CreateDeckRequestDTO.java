package me.fairygel.dto.deck;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDeckRequestDTO(
        @NotBlank(message = "Name cannot be blank")
        @Size(max = 255, message = "Name length must not exceed 255 characters")
        String name,

        @Size(max = 1000, message = "Description length must not exceed 1000 characters")
        String description
) {}