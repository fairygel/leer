package me.fairygel.languageenrichment.dto.explain;

import java.util.List;

public record ExplainResponse (
        String text,
        String language,
        List<ExplainDto> translations
) { }
