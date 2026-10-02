package me.fairygel.service;

import me.fairygel.dto.AuthRequest;
import me.fairygel.entity.User;
import me.fairygel.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    JwtService jwtService;
    @Mock
    BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    AuthService authService;

    private AuthRequest request(String email, String password) {
        AuthRequest r = new AuthRequest();
        r.setEmail(email);
        r.setPassword(password);
        return r;
    }

    @Test
    void register_createsUser_encodesPassword_andReturnsToken() {
        AuthRequest req = request("a@b.c", "secret1");
        when(userRepository.findByEmail("a@b.c")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret1")).thenReturn("HASHED");

        when(jwtService.generateToken(any())).thenReturn("TOKEN");

        String result = authService.registerUser(req);

        assertThat(result).isEqualTo("TOKEN");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_doesNotSave_whenEmailTaken() {
        AuthRequest req = request("a@b.c", "secret1");
        User existing = new User();
        when(userRepository.findByEmail("a@b.c")).thenReturn(Optional.of(existing));

        String result = authService.registerUser(req);

        assertThat(result).isEqualTo("User already Exists");
        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_returnsToken_whenCredentialsValid() {
        AuthRequest req = request("a@b.c", "secret1");
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setPassword("HASHED");
        when(userRepository.findByEmail("a@b.c")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret1", "HASHED")).thenReturn(true);
        when(jwtService.generateToken(user.getId())).thenReturn("TOKEN");

        assertThat(authService.loginUser(req)).isEqualTo("TOKEN");
    }

    @Test
    void login_rejectsUnknownEmail() {
        AuthRequest req = request("no@b.c", "secret1");
        when(userRepository.findByEmail("no@b.c")).thenReturn(Optional.empty());

        assertThat(authService.loginUser(req)).isEqualTo("Invalid Email or Password");
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_rejectsWrongPassword() {
        AuthRequest req = request("a@b.c", "wrong");
        User user = new User();
        user.setPassword("HASHED");
        when(userRepository.findByEmail("a@b.c")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "HASHED")).thenReturn(false);

        assertThat(authService.loginUser(req)).isEqualTo("Invalid Email or Password");
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_doesNotDistinguishUnknownEmailFromWrongPassword() {
        AuthRequest unknown = request("no@b.c", "x");
        when(userRepository.findByEmail("no@b.c")).thenReturn(Optional.empty());

        AuthRequest wrongPass = request("a@b.c", "x");
        User user = new User();
        user.setPassword("HASHED");
        when(userRepository.findByEmail("a@b.c")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("x", "HASHED")).thenReturn(false);

        assertThat(authService.loginUser(unknown))
                .isEqualTo(authService.loginUser(wrongPass));
    }
}
