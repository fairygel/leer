package me.fairygel.languageenrichment.controller;

import lombok.RequiredArgsConstructor;
import me.fairygel.languageenrichment.dto.explain.ExplainRequestDto;
import me.fairygel.languageenrichment.dto.explain.ExplainResponse;
import me.fairygel.languageenrichment.dto.translate.TranslateRequestDto;
import me.fairygel.languageenrichment.dto.translate.TranslateResponse;
import me.fairygel.languageenrichment.service.LanguageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/language")
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    @PostMapping("/translate")
    public TranslateResponse translate(@RequestBody TranslateRequestDto request) {
        return languageService.translate(request);
    }

    @PostMapping("/explain")
    public ExplainResponse explain(@RequestBody ExplainRequestDto request) {
        return languageService.explain(request);
    }
}