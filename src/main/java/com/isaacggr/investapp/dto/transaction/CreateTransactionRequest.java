package com.isaacggr.investapp.dto.transaction;

import com.isaacggr.investapp.enums.AssetType;
import com.isaacggr.investapp.enums.OperationType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(
        @NotBlank @Size(max = 12) String ticker,
        @NotNull AssetType assetType,

        @NotNull OperationType operation,
        @NotNull LocalDate tradeDate,

        @NotNull @DecimalMin(value = "0.000001", inclusive = true)
        BigDecimal quantity,

        @NotNull @DecimalMin(value = "0.000001", inclusive = true)
        BigDecimal unitPrice,

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal fees,

        @Size(max = 255)
        String notes
) {}