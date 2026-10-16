package me.fairygel.languageenrichment.service.translate;

import me.fairygel.languageenrichment.dto.translate.TranslationDto;
import me.fairygel.languageenrichment.service.TranslateService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class VercelTranslate implements TranslateService {
    @Override
    public Set<TranslationDto> getTranslate(String text, String source, String target) {
        return Set.of();
    }
}
