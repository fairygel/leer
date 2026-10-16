package me.fairygel.languageenrichment.dto.translate;

public record TranslationDto (
        String translation,
        String partOfSpeech,
        double relevance
) { }
