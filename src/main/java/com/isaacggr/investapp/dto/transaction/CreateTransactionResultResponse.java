package com.isaacggr.investapp.dto.transaction;

import com.isaacggr.investapp.dto.position.PositionResponse;
import com.isaacggr.investapp.dto.stock.StockResponse;

public record CreateTransactionResultResponse(
        TransactionResponse transaction,
        StockResponse stock,
        PositionResponse position
) {}