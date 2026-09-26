package me.fairygel.dto.card;


import jakarta.validation.constraints.NotNull;

public record UpdateCardStatusRequestDTO(
        @NotNull Boolean isKnown
) {}
