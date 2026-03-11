package com.isaacggr.investapp.controller;

import com.isaacggr.investapp.dto.auth.LoginRequest;
import com.isaacggr.investapp.dto.auth.LoginResponse;
import com.isaacggr.investapp.entity.User;
import com.isaacggr.investapp.exception.ResourceNotFoundException;
import com.isaacggr.investapp.repository.UserRepository;
import com.isaacggr.investapp.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final long tokenExpiration;

    public AuthController(
            UserRepository userRepository,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder,
            @Value("${app.jwt.expiration}") long tokenExpiration
    ) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.tokenExpiration = tokenExpiration;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        // Busca usuário pelo email
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new ResourceNotFoundException("Email ou senha inválidos"));

        // Verifica se está ativo
        if (!user.isActive()) {
            throw new ResourceNotFoundException("Usuário foi deletado");
        }

        // Valida a senha (BCrypt)
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResourceNotFoundException("Email ou senha inválidos");
        }

        // Gera o token JWT
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        // Retorna resposta
        var response = new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                tokenExpiration / 1000  // em segundos
        );

        return ResponseEntity.ok(response);
    }
}
