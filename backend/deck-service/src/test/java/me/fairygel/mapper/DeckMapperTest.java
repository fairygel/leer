package me.fairygel.mapper;

import me.fairygel.dto.deck.CreateDeckRequestDTO;
import me.fairygel.dto.deck.DeckLearnProgressDTO;
import me.fairygel.dto.deck.DeckResponseDTO;
import me.fairygel.dto.deck.UpdateDeckRequestDTO;
import me.fairygel.entity.Deck;
import me.fairygel.enums.Language;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DeckMapperTest {

    private final DeckMapper mapper = new DeckMapperImpl();

    @Test
    void toEntity_mapsFields_andLeavesSecuritySensitiveNull() {
        var dto = new CreateDeckRequestDTO("German A1", "desc", Language.EN, Language.RU);

        Deck entity = mapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("German A1");
        assertThat(entity.getDescription()).isEqualTo("desc");
        assertThat(entity.getSourceLang()).isEqualTo(Language.EN);
        assertThat(entity.getTargetLang()).isEqualTo(Language.RU);

        assertThat(entity.getUserId()).isNull();
        assertThat(entity.getId()).isNull();
    }

    @Test
    void update_ignoresNulls_doesNotOverwrite() {
        Deck deck = new Deck();
        deck.setName("Old");
        deck.setDescription("Old desc");
        deck.setSourceLang(Language.EN);
        deck.setTargetLang(Language.RU);
        UUID userId = UUID.randomUUID();
        deck.setUserId(userId);

        mapper.update(new UpdateDeckRequestDTO("New", null, null, null), deck);

        assertThat(deck.getName()).isEqualTo("New");
        assertThat(deck.getDescription()).isEqualTo("Old desc");
        assertThat(deck.getSourceLang()).isEqualTo(Language.EN);

        assertThat(deck.getUserId()).isEqualTo(userId);
    }

    @Test
    void update_appliesAllNonNull() {
        Deck deck = new Deck();
        deck.setName("Old");

        mapper.update(new UpdateDeckRequestDTO("New", "New desc", Language.RU, Language.EN), deck);

        assertThat(deck.getName()).isEqualTo("New");
        assertThat(deck.getDescription()).isEqualTo("New desc");
        assertThat(deck.getSourceLang()).isEqualTo(Language.RU);
        assertThat(deck.getTargetLang()).isEqualTo(Language.EN);
    }

    @Test
    void toResponse_mapsAllFields() {
        Deck deck = new Deck();
        deck.setId(UUID.randomUUID());
        deck.setName("German A1");
        deck.setSourceLang(Language.EN);
        deck.setTargetLang(Language.RU);

        var response = mapper.toResponse(deck);

        assertThat(response.id()).isEqualTo(deck.getId());
        assertThat(response.name()).isEqualTo("German A1");
        assertThat(response.translation()).isTrue();
    }

    @Test
    void toResponseWithProgress_attachesProgress() {
        Deck deck = new Deck();
        deck.setName("German A1");
        var progress = new DeckLearnProgressDTO(2, 1, 3);

        var response = mapper.toResponseWithProgress(deck, progress);

        assertThat(response.name()).isEqualTo("German A1");
        assertThat(response.learnProgress()).isEqualTo(progress);
        assertThat(response.learnProgress().totalCards()).isEqualTo(6);
    }

    @Test
    void toResponseList_mapsEach() {
        Deck d1 = new Deck();
        d1.setName("One");
        Deck d2 = new Deck();
        d2.setName("Two");

        var result = mapper.toResponseList(List.of(d1, d2));

        assertThat(result).hasSize(2)
                .extracting(DeckResponseDTO::name)
                .containsExactly("One", "Two");
    }
}
