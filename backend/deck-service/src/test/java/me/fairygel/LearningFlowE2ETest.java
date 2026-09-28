package me.fairygel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.fairygel.repository.CardRepository;
import me.fairygel.repository.DeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LearningFlowE2ETest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17.10-alpine");

    @Autowired
    MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    DeckRepository decks;

    @Autowired
    CardRepository cards;

    private final UUID userId = UUID.randomUUID();
    private final UUID strangerId = UUID.randomUUID();

    @BeforeEach
    void clean() {
        cards.deleteAll();
        decks.deleteAll();
    }

    private String userHeader(UUID userId) {
        return userId.toString();
    }

    private UUID createDeck(UUID userId, String name) throws Exception {
        MvcResult result = mvc.perform(post("/api/v1/decks")
                        .header("X-User-Id", userHeader(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","sourceLang":"en","targetLang":"ru"}
                                """.formatted(name)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(body.get("id").asText());
    }

    private UUID createCard(UUID userId, UUID deckId, String question) throws Exception {
        MvcResult result = mvc.perform(post("/api/v1/cards")
                        .header("X-User-Id", userHeader(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"deckId":"%s","question":"%s","answer":"answer of %s"}
                                """.formatted(deckId, question, question)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(body.get("id").asText());
    }

    private void setKnown(UUID userId, UUID cardId, boolean known) throws Exception {
        mvc.perform(patch("/api/v1/cards/{id}/set-known", cardId)
                        .header("X-User-Id", userHeader(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"isKnown":%s}
                                """.formatted(known)))
                .andExpect(status().isOk());
    }

    private JsonNode getLearning(UUID userId, UUID deckId) throws Exception {
        MvcResult result = mvc.perform(get("/api/v1/decks/{id}/learn", deckId)
                        .header("X-User-Id", userHeader(userId)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    @Test
    void fullLearningFlow_newThenUnknownThenReset() throws Exception {
        UUID deckId = createDeck(userId, "German A1");
        UUID card1 = createCard(userId, deckId, "q1");
        UUID card2 = createCard(userId, deckId, "q2");

        assertThat(getLearning(userId, deckId)).hasSize(2);

        setKnown(userId, card1, true);
        JsonNode remaining = getLearning(userId, deckId);
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).get("id").asText()).isEqualTo(card2.toString());

        setKnown(userId, card2, false);
        JsonNode fallback = getLearning(userId, deckId);
        assertThat(fallback).hasSize(1);
        assertThat(fallback.get(0).get("status").asText()).isEqualTo("UNKNOWN");

        mvc.perform(post("/api/v1/decks/{id}/reset-learning", deckId)
                        .header("X-User-Id", userHeader(userId)))
                .andExpect(status().isNoContent());
        JsonNode afterReset = getLearning(userId, deckId);
        assertThat(afterReset).hasSize(2);
        assertThat(afterReset.get(0).get("status").asText()).isEqualTo("NEW");
    }

    @Test
    void usersAreIsolated_endToEnd() throws Exception {
        UUID myDeck = createDeck(userId, "Mine");
        createCard(userId, myDeck, "my q");

        assertThat(getLearning(strangerId, myDeck)).isEmpty();

        mvc.perform(post("/api/v1/cards")
                        .header("X-User-Id", userHeader(strangerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"deckId":"%s","question":"hack","answer":"hack"}
                                """.formatted(myDeck)))
                .andExpect(status().isNotFound());

        assertThat(getLearning(userId, myDeck)).hasSize(1);
    }

    @Test
    void validation_returns400_endToEnd() throws Exception {
        mvc.perform(post("/api/v1/decks")
                        .header("X-User-Id", userHeader(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":""}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deckProgress_reflectsCardStatuses() throws Exception {
        UUID deckId = createDeck(userId, "Progress");
        UUID c1 = createCard(userId, deckId, "q1");
        createCard(userId, deckId, "q2");

        setKnown(userId, c1, true);

        MvcResult result = mvc.perform(get("/api/v1/decks/{id}", deckId)
                        .header("X-User-Id", userHeader(userId)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode progress = body.get("learnProgress");
        assertThat(progress.get("knownCards").asLong()).isEqualTo(1L);
        assertThat(progress.get("newCards").asLong()).isEqualTo(1L);
        assertThat(progress.get("totalCards").asLong()).isEqualTo(2L);
    }
}
