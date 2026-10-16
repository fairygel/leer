package me.fairygel.languageenrichment.dto.translate;

import java.util.List;

public record TranslateResponse (
        String text,
        String sourceLanguage,
        String targetLanguage,
        List<TranslationDto> translations
) { }
