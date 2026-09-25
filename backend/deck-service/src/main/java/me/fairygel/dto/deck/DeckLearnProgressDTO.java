package me.fairygel.dto.deck;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeckLearnProgressDTO(
        long newCards,
        long unknownCards,
        long knownCards
) {
    public DeckLearnProgressDTO {
        if (newCards < 0 || unknownCards < 0 || knownCards < 0) {
            throw new IllegalArgumentException("Card counters must be non-negative");
        }
    }

    @JsonProperty("totalCards")
    public long totalCards() {
        return newCards + unknownCards + knownCards;
    }

    @JsonProperty("remainToLearn")
    public long remainToLearn() {
        return newCards + unknownCards;
    }

    @JsonProperty("progress")
    public double progress() {
        long total = totalCards();
        return total == 0 ? 0 : (double) knownCards / total;
    }
}
