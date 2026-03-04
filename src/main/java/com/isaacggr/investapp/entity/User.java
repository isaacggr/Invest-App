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

    public User(String name, String email, String passwordHash) {
        String normalizedName = normalizeName(name);
        String normalizedEmail = normalizeEmail(email);

        validarName(normalizedName);
        validarEmail(normalizedEmail);
        validarPasswordHash(passwordHash);

        this.name = normalizedName;
        this.email = normalizedEmail;
        this.passwordHash = passwordHash;
    }

    public boolean isActive() {
        return !this.deleted;
    }

    public void markAsDeleted() {
        this.deleted = true;
    }

    public void changeName(String name) {
        String normalizedName = normalizeName(name);
        validarName(normalizedName);
        this.name = normalizedName;
    }

    public void changePasswordHash(String newPasswordHash) {
        validarPasswordHash(newPasswordHash);
        this.passwordHash = newPasswordHash;
    }

    public void changeEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        validarEmail(normalizedEmail);
        this.email = normalizedEmail;
    }

    private String normalizeName(String name) {
        return name == null ? null : name.trim();
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
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
        if (email.contains(" ")) {
            throw new IllegalArgumentException("Email inválido");
        }

        int at = email.indexOf('@');
        if (at <= 0 || at != email.lastIndexOf('@') || at == email.length() - 1) {
            throw new IllegalArgumentException("Email inválido");
        }

        int dotAfterAt = email.indexOf('.', at + 2);
        if (dotAfterAt == -1 || dotAfterAt == email.length() - 1) {
            throw new IllegalArgumentException("Email inválido");
        }
    }

    private void validarPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Senha (hash) é obrigatória");
        }
    }
}