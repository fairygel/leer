package me.fairygel.controller;

import me.fairygel.dto.deck.DeckResponseDTO;
import me.fairygel.enums.Language;
import me.fairygel.service.DeckService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeckController.class)
class DeckControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    DeckService deckService;

    private final UUID userId = UUID.randomUUID();

    @Test
    void createDeck_returns201_whenValid() throws Exception {
        when(deckService.create(any(), any())).thenReturn(
                new DeckResponseDTO(UUID.randomUUID(), "German A1", null,
                        Language.EN, Language.RU, null, null, null));

        mvc.perform(post("/api/v1/decks")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"German A1","sourceLang":"en","targetLang":"ru"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("German A1"));

        verify(deckService).create(eq(userId), any());
    }

    @Test
    void createDeck_returns400_andDoesNotCallService_whenNameBlank() throws Exception {
        mvc.perform(post("/api/v1/decks")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","sourceLang":"en","targetLang":"ru"}
                                """))
                .andExpect(status().isBadRequest());

        verify(deckService, never()).create(any(), any());
    }

    @Test
    void getDecks_returnsList() throws Exception {
        when(deckService.findAllByUserId(userId)).thenReturn(List.of(
                new DeckResponseDTO(UUID.randomUUID(), "German A1", null,
                        Language.EN, Language.RU, null, null, null)));

        mvc.perform(get("/api/v1/decks")
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("German A1"));

        verify(deckService).findAllByUserId(userId);
    }

    @Test
    void deleteDeck_returns204() throws Exception {
        UUID deckId = UUID.randomUUID();

        mvc.perform(delete("/api/v1/decks/{deckId}", deckId)
                        .header("X-User-Id", userId))
                .andExpect(status().isNoContent());

        verify(deckService).deleteByIdAndUserId(userId, deckId);
    }
}
