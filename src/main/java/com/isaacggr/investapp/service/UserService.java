package com.isaacggr.investapp.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.isaacggr.investapp.dto.user.CreateUserRequest;
import com.isaacggr.investapp.dto.user.UpdateUserNameRequest;
import com.isaacggr.investapp.dto.user.UserResponse;
import com.isaacggr.investapp.entity.User;
import com.isaacggr.investapp.exception.BusinessRuleException;
import com.isaacggr.investapp.exception.ResourceNotFoundException;
import com.isaacggr.investapp.repository.UserRepository;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ======================
    // CREATE
    // ======================
    public UserResponse create(CreateUserRequest req) {
        if (req == null) throw new IllegalArgumentException("Body não pode ser nulo");

        String emailNormalized = normalizeEmail(req.email());

        if (userRepository.existsByEmail(emailNormalized)) {
            throw new BusinessRuleException("Email já cadastrado");
        }

        String hash = passwordEncoder.encode(req.password());

        // regras do domínio rodam aqui dentro (construtor valida)
        User user = new User(req.name(), emailNormalized, hash);

        userRepository.save(user);
        return toResponse(user);
    }

    // ======================
    // READ
    // ======================
    public UserResponse getById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return toResponse(user);
    }

    // ======================
    // UPDATE (nome)
    // ======================
    public UserResponse updateName(UUID id, UpdateUserNameRequest req) {
        if (req == null) throw new IllegalArgumentException("Body não pode ser nulo");

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // regra fica na entity
        user.changeName(req.name());

        userRepository.save(user);
        return toResponse(user);
    }

    // ======================
    // DELETE (soft delete)
    // ======================
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        user.markAsDeleted();

        userRepository.save(user);
    }

    // ======================
    // HELPERS
    // ======================
    private String normalizeEmail(String email) {
        if (email == null) throw new IllegalArgumentException("Email é obrigatório");
        return email.trim().toLowerCase();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isActive()
        );
    }
}