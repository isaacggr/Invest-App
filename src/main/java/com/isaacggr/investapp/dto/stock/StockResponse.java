package com.isaacggr.investapp.dto.stock;

import com.isaacggr.investapp.enums.AssetType;

import java.util.UUID;

public record StockResponse(
        UUID id,
        String ticker,
        AssetType type,
        String name,
        String exchange,
        boolean active
) {}