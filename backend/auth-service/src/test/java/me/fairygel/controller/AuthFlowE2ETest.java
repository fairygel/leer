package me.fairygel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.fairygel.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AuthFlowE2ETest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17.10-alpine");

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository users;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void clean() {
        users.deleteAll();
    }

    private String body(String email, String password) throws Exception {
        return objectMapper.writeValueAsString(
                java.util.Map.of("email", email, "password", password));
    }

    @Test
    void register_thenLogin_returnsJwt() throws Exception {
        MvcResult registered = mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("user@mail.com", "secret1")))
                .andExpect(status().isOk())
                .andReturn();

        String registerToken = registered.getResponse().getContentAsString();
        assertThat(registerToken).doesNotContain("Exists").hasSizeGreaterThan(20);

        MvcResult loggedIn = mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("user@mail.com", "secret1")))
                .andExpect(status().isOk())
                .andReturn();

        String loginToken = loggedIn.getResponse().getContentAsString();
        assertThat(loginToken).doesNotContain("Invalid").hasSizeGreaterThan(20);
    }

    @Test
    void register_duplicateEmail_doesNotCreateSecondUser() throws Exception {
        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("dup@mail.com", "secret1")))
                .andExpect(status().isOk());

        MvcResult second = mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("dup@mail.com", "secret1")))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(second.getResponse().getContentAsString()).contains("Exists");
        assertThat(users.count()).isEqualTo(1);
    }

    @Test
    void login_wrongPassword_rejected() throws Exception {
        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("user@mail.com", "secret1")))
                .andExpect(status().isOk());

        MvcResult result = mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("user@mail.com", "wrongpass")))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).contains("Invalid");
    }

    @Test
    void validation_rejectsBadEmail_andShortPassword() throws Exception {
        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("not-an-email", "secret1")))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("user@mail.com", "123")))
                .andExpect(status().isBadRequest());

        assertThat(users.count()).isZero();
    }
}
