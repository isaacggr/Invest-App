package com.isaacggr.investapp.entity;

import com.isaacggr.investapp.enums.AssetType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "stocks",
        uniqueConstraints = @UniqueConstraint(name = "uk_stock_ticker", columnNames = "ticker")
)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 12, updatable = false)
    private String ticker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssetType type;

    @Column(length = 120)
    private String name;

    @Column(length = 20)
    private String exchange;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public Stock(String ticker, AssetType type, String name, String exchange) {
        if (ticker == null || ticker.isBlank()) {
            throw new IllegalArgumentException("Ticker é obrigatório");
        }
        if (type == null) {
            throw new IllegalArgumentException("Tipo do ativo é obrigatório");
        }

        this.ticker = ticker.trim().toUpperCase();
        this.type = type;
        this.name = name == null ? null : name.trim();
        this.exchange = exchange == null ? null : exchange.trim().toUpperCase();
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
        normalize();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
        normalize();
    }

    private void normalize() {
        if (exchange != null) exchange = exchange.trim().toUpperCase();
        if (name != null) name = name.trim();
    }
}