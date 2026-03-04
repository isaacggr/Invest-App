package com.isaacggr.investapp.dto.position;

import java.math.BigDecimal;
import java.util.UUID;

public record PositionResponse(
        UUID stockId,
        String ticker,
        BigDecimal quantity,
        BigDecimal avgPrice,
        BigDecimal totalCost
) {}