package me.fairygel.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.fairygel.dto.AuthRequest;
import me.fairygel.entity.User;
import me.fairygel.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public String registerUser(AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "User already Exists";
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return jwtService.generateToken(user.getId());
    }

    public String loginUser(AuthRequest request) {
        val user = userRepository.findByEmail(request.getEmail());

        if (user.isEmpty() || !passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return "Invalid Email or Password";
        }

        return jwtService.generateToken(user.get().getId());
    }
}
