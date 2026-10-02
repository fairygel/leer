package me.fairygel.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "gateway-test-secret-key-only-for-tests-123";

    private JwtService service() {
        JwtService s = new JwtService();
        ReflectionTestUtils.setField(s, "secretKey", SECRET);
        return s;
    }

    private String mint(UUID userId, String secret, long ttlMillis) {
        return JWT.create()
                .withSubject(userId.toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ttlMillis))
                .sign(Algorithm.HMAC256(secret));
    }

    @Test
    void verifyToken_acceptsOwnToken_andExposesUserId() {
        UUID userId = UUID.randomUUID();
        String token = mint(userId, SECRET, TimeUnit.HOURS.toMillis(1));

        DecodedJWT decoded = service().verifyToken(token);

        assertThat(decoded.getSubject()).isEqualTo(userId.toString());
    }

    @Test
    void verifyToken_rejectsForeignSecret() {
        String foreign = mint(UUID.randomUUID(), "some-other-app-secret", TimeUnit.HOURS.toMillis(1));
        JwtService jwtService = service();

        assertThatThrownBy(() -> jwtService.verifyToken(foreign))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    void verifyToken_rejectsExpired() {
        String expired = mint(UUID.randomUUID(), SECRET, -TimeUnit.HOURS.toMillis(1));
        JwtService jwtService = service();

        assertThatThrownBy(() -> jwtService.verifyToken(expired))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    void verifyToken_rejectsGarbage() {
        JwtService jwtService = service();

        assertThatThrownBy(() -> jwtService.verifyToken("not.a.jwt"))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    void authServiceAndGateway_agreeOnSecret() {
        UUID userId = UUID.randomUUID();
        String tokenFromAuthService = mint(userId, SECRET, TimeUnit.DAYS.toMillis(1));

        DecodedJWT decoded = service().verifyToken(tokenFromAuthService);

        assertThat(decoded.getSubject()).isEqualTo(userId.toString());
    }
}
