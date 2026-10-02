package me.fairygel.controller;

import me.fairygel.dto.card.CardResponseDTO;
import me.fairygel.enums.CardStatus;
import me.fairygel.service.CardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    CardService cardService;

    private final UUID userId = UUID.randomUUID();

    @Test
    void createCard_returns201_whenValid() throws Exception {
        UUID deckId = UUID.randomUUID();
        when(cardService.create(any(), any())).thenReturn(
                new CardResponseDTO(UUID.randomUUID(), deckId, "q", "a",
                        null, CardStatus.NEW, null, null, null));

        mvc.perform(post("/api/v1/cards")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"deckId":"%s","question":"q","answer":"a"}
                                """.formatted(deckId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.question").value("q"));

        verify(cardService).create(eq(userId), any());
    }

    @Test
    void createCard_returns400_andDoesNotCallService_whenQuestionBlank() throws Exception {
        mvc.perform(post("/api/v1/cards")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"deckId":"%s","question":"","answer":"a"}
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).create(any(), any());
    }

    @Test
    void getCard_returns200() throws Exception {
        UUID cardId = UUID.randomUUID();
        when(cardService.findByIdAndUserId(userId, cardId)).thenReturn(
                new CardResponseDTO(cardId, UUID.randomUUID(), "q", "a",
                        null, CardStatus.NEW, null, null, null));

        mvc.perform(get("/api/v1/cards/{cardId}", cardId)
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("q"));
    }

    @Test
    void deleteCard_returns204() throws Exception {
        UUID cardId = UUID.randomUUID();

        mvc.perform(delete("/api/v1/cards/{cardId}", cardId)
                        .header("X-User-Id", userId))
                .andExpect(status().isNoContent());

        verify(cardService).deleteByIdAndUserId(userId, cardId);
    }
}
