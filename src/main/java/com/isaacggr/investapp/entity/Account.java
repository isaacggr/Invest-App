package com.isaacggr.investapp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    // ======================
    // RELACIONAMENTO COM USER
    // ======================
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false)
    private boolean deleted = false;

    // ======================
    // CONSTRUTOR DE DOMÍNIO
    // ======================
    public Account(User user, String name) {
        if (user == null) {
            throw new IllegalArgumentException("Usuário é obrigatório");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome da carteira é obrigatório");
        }

        this.user = user;
        this.name = name.trim();
    }

    // ======================
    // REGRAS DE ESTADO
    // ======================
    public boolean isActive() {
        return !deleted;
    }

    public void markAsDeleted() {
        this.deleted = true;
    }

    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome da carteira inválido");
        }
        this.name = name.trim();
    }

    // útil futuramente
    public UUID getUserId() {
        return user.getId();
    }
}