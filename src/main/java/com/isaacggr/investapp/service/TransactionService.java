package com.isaacggr.investapp.service;

import com.isaacggr.investapp.dto.position.PositionResponse;
import com.isaacggr.investapp.dto.stock.StockResponse;
import com.isaacggr.investapp.dto.transaction.CreateTransactionRequest;
import com.isaacggr.investapp.dto.transaction.CreateTransactionResultResponse;
import com.isaacggr.investapp.dto.transaction.TransactionResponse;
import com.isaacggr.investapp.entity.Account;
import com.isaacggr.investapp.entity.Stock;
import com.isaacggr.investapp.entity.Transaction;
import com.isaacggr.investapp.exception.BusinessRuleException;
import com.isaacggr.investapp.exception.ResourceNotFoundException;
import com.isaacggr.investapp.repository.AccountRepository;
import com.isaacggr.investapp.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final StockService stockService;
    private final PositionService positionService;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            StockService stockService,
            PositionService positionService
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.stockService = stockService;
        this.positionService = positionService;
    }

    @Transactional
    public CreateTransactionResultResponse create(UUID accountId, CreateTransactionRequest req) {
        if (accountId == null) throw new IllegalArgumentException("accountId é obrigatório");
        if (req == null) throw new IllegalArgumentException("Body não pode ser nulo");

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        if (!account.isActive()) {
            throw new BusinessRuleException("Carteira inativa");
        }

        // ✅ cria ou retorna existente + valida tipo
        Stock stock = stockService.ensureExists(req.ticker(), req.assetType());

        if (!stock.isActive()) {
            throw new BusinessRuleException("Ativo inativo");
        }

        Transaction tx = new Transaction(
                account,
                stock,
                req.operation(),
                req.tradeDate(),
                req.quantity(),
                req.unitPrice(),
                req.fees(),
                req.notes()
        );

        transactionRepository.save(tx);

        TransactionResponse txResponse = new TransactionResponse(
                tx.getId(),
                account.getId(),
                stock.getId(),
                stock.getTicker(),
                tx.getOperation(),
                tx.getTradeDate(),
                tx.getQuantity(),
                tx.getUnitPrice(),
                tx.getFees(),
                tx.getNotes()
        );

        StockResponse stockResponse = stockService.toResponse(stock);

        PositionResponse positionResponse =
                positionService.calculatePosition(accountId, stock.getId(), stock.getTicker());

        return new CreateTransactionResultResponse(txResponse, stockResponse, positionResponse);
    }
}