package me.fairygel.languageenrichment.service;

import me.fairygel.languageenrichment.dto.explain.ExplainDto;

import java.util.Set;

public interface ExplainService {
    Set<ExplainDto> getExplain(String text, String language);
}
