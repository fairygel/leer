package me.fairygel.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "test-secret-key-for-unit-tests-only-12345";

    private JwtService service() {
        JwtService s = new JwtService();
        ReflectionTestUtils.setField(s, "secretKey", SECRET);
        return s;
    }

    @Test
    void generateToken_embedsUserIdAsSubject_andIsVerifiable() {
        UUID userId = UUID.randomUUID();

        String token = service().generateToken(userId);

        DecodedJWT decoded = JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
        assertThat(decoded.getSubject()).isEqualTo(userId.toString());
    }

    @Test
    void generateToken_setsExpiryAboutOneDayAhead() {
        String token = service().generateToken(UUID.randomUUID());

        DecodedJWT decoded = JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
        long ttl = decoded.getExpiresAt().getTime() - decoded.getIssuedAt().getTime();
        assertThat(ttl).isBetween(
                TimeUnit.HOURS.toMillis(23), TimeUnit.HOURS.toMillis(25));
    }

    @Test
    void generateToken_signedWithDifferentSecret_failsVerification() {
        String token = service().generateToken(UUID.randomUUID());
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256("other-secret")).build();

        assertThatThrownBy(() -> verifier.verify(token))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    void expiredToken_failsVerification() {
        String expired = JWT.create()
                .withSubject(UUID.randomUUID().toString())
                .withIssuedAt(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2)))
                .withExpiresAt(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)))
                .sign(Algorithm.HMAC256(SECRET));
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();

        assertThatThrownBy(() -> verifier.verify(expired))
                .isInstanceOf(JWTVerificationException.class);
    }
}
