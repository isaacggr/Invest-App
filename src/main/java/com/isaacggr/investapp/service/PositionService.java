package com.isaacggr.investapp.service;

import com.isaacggr.investapp.dto.position.PositionResponse;
import com.isaacggr.investapp.entity.Transaction;
import com.isaacggr.investapp.enums.OperationType;
import com.isaacggr.investapp.exception.BusinessRuleException;
import com.isaacggr.investapp.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PositionService {

    private final TransactionRepository transactionRepository;

    public PositionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // ✅ NÃO MUDEI A ASSINATURA (para não quebrar o TransactionService)
    @Transactional(readOnly = true)
    public PositionResponse calculatePosition(UUID accountId, UUID stockId, String ticker) {
        if (accountId == null) throw new IllegalArgumentException("accountId é obrigatório");
        if (stockId == null) throw new IllegalArgumentException("stockId é obrigatório");
        if (ticker == null || ticker.isBlank()) throw new IllegalArgumentException("ticker é obrigatório");

        List<Transaction> txs = transactionRepository
                .findByAccount_IdAndStock_IdOrderByTradeDateAscCreatedAtAsc(accountId, stockId);

        Acc acc = new Acc(stockId, normalizeTicker(ticker));

        for (Transaction tx : txs) {
            apply(acc, tx);
        }

        return new PositionResponse(stockId, acc.ticker, acc.qty, acc.avg, acc.totalCost);
    }

    @Transactional(readOnly = true)
    public BigDecimal getCurrentQuantity(UUID accountId, UUID stockId) {
        if (accountId == null) throw new IllegalArgumentException("accountId é obrigatório");
        if (stockId == null) throw new IllegalArgumentException("stockId é obrigatório");

        List<Transaction> txs = transactionRepository
                .findByAccount_IdAndStock_IdOrderByTradeDateAscCreatedAtAsc(accountId, stockId);

        BigDecimal qty = BigDecimal.ZERO;

        for (Transaction tx : txs) {
            qty = tx.getOperation() == OperationType.BUY
                    ? qty.add(tx.getQuantity())
                    : qty.subtract(tx.getQuantity());
        }

        return qty;
    }

    // ✅ LISTAR TODAS AS POSIÇÕES DA ACCOUNT
    @Transactional(readOnly = true)
    public List<PositionResponse> calculateAllPositions(UUID accountId) {
        if (accountId == null) throw new IllegalArgumentException("accountId é obrigatório");

        List<Transaction> txs = transactionRepository
                .findByAccount_IdOrderByTradeDateAscCreatedAtAsc(accountId);

        Map<UUID, Acc> map = new HashMap<>();

        for (Transaction tx : txs) {
            UUID stockId = tx.getStock().getId();
            String ticker = tx.getStock().getTicker();

            Acc acc = map.computeIfAbsent(stockId, id -> new Acc(stockId, normalizeTicker(ticker)));
            apply(acc, tx);
        }

        return map.values().stream()
                .filter(a -> a.qty.compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(a -> a.ticker))
                .map(a -> new PositionResponse(a.stockId, a.ticker, a.qty, a.avg, a.totalCost))
                .toList();
    }

    // ======================
    // CÁLCULO CENTRALIZADO
    // ======================
    private void apply(Acc acc, Transaction tx) {
        BigDecimal q = tx.getQuantity();
        BigDecimal value = tx.getUnitPrice().multiply(q);
        BigDecimal fees = tx.getFees() == null ? BigDecimal.ZERO : tx.getFees();

        if (tx.getOperation() == OperationType.BUY) {
            acc.totalCost = acc.totalCost.add(value).add(fees);
            acc.qty = acc.qty.add(q);

            acc.avg = acc.qty.signum() == 0
                    ? BigDecimal.ZERO
                    : acc.totalCost.divide(acc.qty, 6, RoundingMode.HALF_UP);
            return;
        }

        // SELL
        if (q.compareTo(acc.qty) > 0) {
            throw new BusinessRuleException(
                    "Venda inválida: tentando vender mais do que a quantidade em carteira para " + acc.ticker
            );
        }

        acc.qty = acc.qty.subtract(q);

        if (acc.qty.signum() == 0) {
            acc.totalCost = BigDecimal.ZERO;
            acc.avg = BigDecimal.ZERO;
        } else {
            // mantém o avg e ajusta custo proporcional (seu comportamento original)
            acc.totalCost = acc.avg.multiply(acc.qty).setScale(6, RoundingMode.HALF_UP);
        }
    }

    private String normalizeTicker(String ticker) {
        return ticker == null ? null : ticker.trim().toUpperCase();
    }

    private static class Acc {
        UUID stockId;
        String ticker;
        BigDecimal qty = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal avg = BigDecimal.ZERO;

        Acc(UUID stockId, String ticker) {
            this.stockId = stockId;
            this.ticker = ticker;
        }
    }
}