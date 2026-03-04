package com.isaacggr.investapp.entity;

import com.isaacggr.investapp.enums.OperationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "transactions",
        indexes = {
                @Index(name = "ix_tx_account_date", columnList = "account_id, tradeDate"),
                @Index(name = "ix_tx_stock", columnList = "stock_id")
        }
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OperationType operation;

    @Column(nullable = false)
    private LocalDate tradeDate;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal fees = BigDecimal.ZERO;

    @Column(length = 255)
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Transaction(
            Account account,
            Stock stock,
            OperationType operation,
            LocalDate tradeDate,
            BigDecimal quantity,
            BigDecimal unitPrice,
            BigDecimal fees,
            String notes
    ) {
        validar(account, stock, operation, tradeDate, quantity, unitPrice, fees);

        this.account = account;
        this.stock = stock;
        this.operation = operation;
        this.tradeDate = tradeDate;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.fees = (fees == null ? BigDecimal.ZERO : fees);
        this.notes = normalizeNotes(notes);
    }

    public void changeNotes(String notes) {
        this.notes = normalizeNotes(notes);
    }

    public void changeFees(BigDecimal fees) {
        if (fees == null) fees = BigDecimal.ZERO;
        if (fees.signum() < 0) throw new IllegalArgumentException("Fees não pode ser negativo");
        this.fees = fees;
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        if (fees == null) fees = BigDecimal.ZERO;
        notes = normalizeNotes(notes);
    }

    private void validar(
            Account account,
            Stock stock,
            OperationType operation,
            LocalDate tradeDate,
            BigDecimal quantity,
            BigDecimal unitPrice,
            BigDecimal fees
    ) {
        if (account == null) throw new IllegalArgumentException("Account é obrigatória");
        if (stock == null) throw new IllegalArgumentException("Stock é obrigatória");
        if (operation == null) throw new IllegalArgumentException("Operation é obrigatória");
        if (tradeDate == null) throw new IllegalArgumentException("tradeDate é obrigatória");
        if (quantity == null || quantity.signum() <= 0) throw new IllegalArgumentException("quantity deve ser > 0");
        if (unitPrice == null || unitPrice.signum() < 0) throw new IllegalArgumentException("unitPrice não pode ser negativo");
        if (fees != null && fees.signum() < 0) throw new IllegalArgumentException("fees não pode ser negativo");
    }

    private String normalizeNotes(String notes) {
        if (notes == null) return null;
        String n = notes.trim();
        return n.isEmpty() ? null : n;
    }
}