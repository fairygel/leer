package me.fairygel.languageenrichment.service;

import me.fairygel.languageenrichment.dto.translate.TranslationDto;

import java.util.Set;

public interface TranslateService {
    Set<TranslationDto> getTranslate(String text, String source, String target);
}
