package me.fairygel.languageenrichment.service;

import lombok.RequiredArgsConstructor;
import me.fairygel.languageenrichment.dto.explain.ExplainRequestDto;
import me.fairygel.languageenrichment.dto.explain.ExplainResponse;
import me.fairygel.languageenrichment.dto.translate.TranslateRequestDto;
import me.fairygel.languageenrichment.dto.translate.TranslateResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageService {
    private final List<TranslateService> translateServiceList;
    private final List<ExplainService> explainServiceList;

    public TranslateResponse translate(TranslateRequestDto request) {

        return null;
    }

    public ExplainResponse explain(ExplainRequestDto request) {

        return null;
    }
}
