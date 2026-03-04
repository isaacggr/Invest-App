package com.isaacggr.investapp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
    name = "accounts",
    indexes = {
        @Index(name = "ix_accounts_user", columnList = "user_id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false)
    private boolean deleted = false;

    public Account(User user, String name) {
        if (user == null) {
            throw new IllegalArgumentException("Usuário é obrigatório");
        }
        String normalizedName = normalizeName(name);
        validarName(normalizedName);

        this.user = user;
        this.name = normalizedName;
    }

    public boolean isActive() {
        return !deleted;
    }

    public void markAsDeleted() {
        this.deleted = true;
    }

    public void changeName(String name) {
        String normalizedName = normalizeName(name);
        validarName(normalizedName);
        this.name = normalizedName;
    }

    private String normalizeName(String name) {
        return name == null ? null : name.trim();
    }

    private void validarName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome da carteira é obrigatório");
        }
        if (name.length() > 80) {
            throw new IllegalArgumentException("Nome da carteira deve ter no máximo 80 caracteres");
        }
    }
}