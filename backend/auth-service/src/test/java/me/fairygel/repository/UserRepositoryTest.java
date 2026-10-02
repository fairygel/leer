package me.fairygel.repository;

import me.fairygel.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17.10-alpine");

    @Autowired
    UserRepository users;

    @Autowired
    TestEntityManager em;

    private User persistUser() {
        User u = new User();
        u.setEmail("a@b.c");
        u.setPassword("HASHED");
        return em.persistAndFlush(u);
    }

    @Test
    void findByEmail_returnsUser_whenExists() {
        persistUser();

        assertThat(users.findByEmail("a@b.c")).isPresent();
    }

    @Test
    void findByEmail_returnsEmpty_whenUnknown() {
        persistUser();

        assertThat(users.findByEmail("nope@b.c")).isEmpty();
    }

    @Test
    void generatedId_isUuid() {
        User saved = persistUser();

        assertThat(saved.getId()).isNotNull().isInstanceOf(UUID.class);
    }
}
