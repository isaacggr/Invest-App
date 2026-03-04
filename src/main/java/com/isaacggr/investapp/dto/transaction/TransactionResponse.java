package com.isaacggr.investapp.dto.transaction;

import com.isaacggr.investapp.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID accountId,
        UUID stockId,
        String ticker,
        OperationType operation,
        LocalDate tradeDate,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal fees,
        String notes
) {}