package me.fairygel.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import me.fairygel.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtGatewayFilterTest {

    @Mock
    JwtService jwtService;

    @InjectMocks
    JwtGatewayFilter filter;

    private MockServerWebExchange exchange(String path, String authHeader) {
        MockServerHttpRequest.BaseBuilder<?> builder = MockServerHttpRequest.get(path);
        if (authHeader != null) {
            builder.header(HttpHeaders.AUTHORIZATION, authHeader);
        }
        return MockServerWebExchange.from(builder);
    }

    @Test
    void publicPaths_passThrough_withoutJwtCheck() {
        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        for (String path : new String[]{
                "/actuator/health",
                "/api/v1/auth/login",
                "/api/v1/auth/register"}) {
            StepVerifier.create(filter.filter(exchange(path, null), chain))
                    .verifyComplete();
        }

        verify(jwtService, never()).verifyToken(any());
    }

    @Test
    void missingHeader_returns401() {
        WebFilterChain chain = mock(WebFilterChain.class);

        MockServerWebExchange exchange = exchange("/api/v1/decks", null);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void malformedHeader_returns401() {
        WebFilterChain chain = mock(WebFilterChain.class);

        MockServerWebExchange exchange = exchange("/api/v1/decks", "Token abc");

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void invalidToken_returns401() {
        WebFilterChain chain = mock(WebFilterChain.class);
        when(jwtService.verifyToken("bad")).thenThrow(new RuntimeException("bad signature"));

        MockServerWebExchange exchange = exchange("/api/v1/decks", "Bearer bad");

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void validToken_addsXUserIdHeader_andContinues() {
        String userId = UUID.randomUUID().toString();
        DecodedJWT decoded = mock(DecodedJWT.class);
        when(decoded.getSubject()).thenReturn(userId);
        when(jwtService.verifyToken("good")).thenReturn(decoded);

        final ServerWebExchange[] downstream = new ServerWebExchange[1];
        WebFilterChain chain = e -> {
            downstream[0] = e;
            return Mono.empty();
        };

        MockServerWebExchange exchange = exchange("/api/v1/decks", "Bearer good");

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(downstream[0]).isNotNull();
        assertThat(downstream[0].getRequest().getHeaders().getFirst("X-User-Id"))
                .isEqualTo(userId);
        assertThat(exchange.getResponse().getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
