package me.fairygel.languageenrichment.service.explain;

import me.fairygel.languageenrichment.dto.explain.ExplainDto;
import me.fairygel.languageenrichment.service.ExplainService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class VercelExplain implements ExplainService {
    @Override
    public Set<ExplainDto> getExplain(String text, String language) {
        return Set.of();
    }
}
