package com.isaacggr.investapp.service;

import com.isaacggr.investapp.dto.stock.StockResponse;
import com.isaacggr.investapp.enums.AssetType;
import com.isaacggr.investapp.exception.BusinessRuleException;
import com.isaacggr.investapp.entity.Stock;
import com.isaacggr.investapp.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
public Stock ensureExists(String tickerRaw, AssetType type) {
    if (tickerRaw == null || tickerRaw.isBlank()) {
        throw new IllegalArgumentException("ticker é obrigatório");
    }
    if (type == null) {
        throw new IllegalArgumentException("assetType é obrigatório");
    }

    String ticker = tickerRaw.trim().toUpperCase();

    Stock stock = stockRepository.findByTicker(ticker)
            .orElseGet(() -> stockRepository.save(new Stock(ticker, type, null, null)));

    if (stock.getType() != type) {
        throw new BusinessRuleException("Tipo de ativo não corresponde ao ticker");
    }

    return stock;
}

    public StockResponse toResponse(Stock s) {
        return new StockResponse(
                s.getId(),
                s.getTicker(),
                s.getType(),
                s.getName(),
                s.getExchange(),
                s.isActive()
        );
    }
}