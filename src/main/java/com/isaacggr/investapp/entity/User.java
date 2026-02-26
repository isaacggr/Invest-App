package com.isaacggr.investapp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 80, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false)
    private boolean deleted = false;

    // Construtor de domínio
    public User(String name, String email, String passwordHash) {
        validarName(name);
        validarEmail(email);
        validarPasswordHash(passwordHash);

        this.name = name.trim();
        this.email = email.trim().toLowerCase();
        this.passwordHash = passwordHash;
    }

    public boolean isActive() {
        return !this.deleted;
    }

    public void markAsDeleted() {
        this.deleted = true;
    }

    public void changeName(String name) {
        validarName(name);
        this.name = name.trim();
    }

    public void changePasswordHash(String newPasswordHash) {
        validarPasswordHash(newPasswordHash);
        this.passwordHash = newPasswordHash;
    }

    public void changeEmail(String email) {
        validarEmail(email);
        this.email = email.trim().toLowerCase();
    }


    private void validarName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("Nome deve ter no máximo 50 caracteres");
        }
    }

    private void validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (email.length() > 80) {
            throw new IllegalArgumentException("Email deve ter no máximo 80 caracteres");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }

    private void validarPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Senha (hash) é obrigatória");
        }
    }
}