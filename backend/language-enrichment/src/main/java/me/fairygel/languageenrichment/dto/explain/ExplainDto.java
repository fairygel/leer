package me.fairygel.languageenrichment.dto.explain;

public record ExplainDto (
        String explanation,
        double relevance
) { }