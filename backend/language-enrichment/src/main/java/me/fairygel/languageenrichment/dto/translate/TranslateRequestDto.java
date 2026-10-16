package me.fairygel.languageenrichment.dto.translate;

public record TranslateRequestDto(
        String text,
        String source,
        String target
) { }
