package me.fairygel.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.fairygel.dto.AuthRequest;
import me.fairygel.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public String login(@Valid @RequestBody AuthRequest request) {
        return authService.loginUser(request);
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody AuthRequest request) {
        return authService.registerUser(request);
    }
}
