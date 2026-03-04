package com.isaacggr.investapp.controller;

import com.isaacggr.investapp.dto.transaction.CreateTransactionRequest;
import com.isaacggr.investapp.dto.transaction.CreateTransactionResultResponse;
import com.isaacggr.investapp.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/accounts/{accountId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateTransactionResultResponse create(
            @PathVariable UUID accountId,
            @RequestBody @Valid CreateTransactionRequest request
    ) {
        return transactionService.create(accountId, request);
    }
}