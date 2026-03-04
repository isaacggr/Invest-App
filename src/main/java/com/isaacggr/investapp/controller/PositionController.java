package com.isaacggr.investapp.controller;

import com.isaacggr.investapp.dto.position.PositionResponse;
import com.isaacggr.investapp.entity.Stock;
import com.isaacggr.investapp.exception.ResourceNotFoundException;
import com.isaacggr.investapp.repository.StockRepository;
import com.isaacggr.investapp.service.PositionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts/{accountId}/positions")
public class PositionController {

    private final PositionService positionService;
    private final StockRepository stockRepository;

    public PositionController(PositionService positionService, StockRepository stockRepository) {
        this.positionService = positionService;
        this.stockRepository = stockRepository;
    }

    @GetMapping("/{stockId}")
    public PositionResponse getPosition(
            @PathVariable UUID accountId,
            @PathVariable UUID stockId
    ) {
        Stock stock = findStockById(stockId);
        return positionService.calculatePosition(accountId, stockId, stock.getTicker());
    }

    @GetMapping("/ticker/{ticker}")
    public PositionResponse getPositionByTicker(
            @PathVariable UUID accountId,
            @PathVariable String ticker
    ) {
        String normalized = normalizeTicker(ticker);
        Stock stock = findStockByTicker(normalized);

        return positionService.calculatePosition(accountId, stock.getId(), stock.getTicker());
    }

    @GetMapping
    public List<PositionResponse> listAllPositions(@PathVariable UUID accountId) {
        return positionService.calculateAllPositions(accountId);
    }

    // ======================
    // HELPERS
    // ======================
    private Stock findStockById(UUID stockId) {
        return stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock não encontrado: " + stockId));
    }

    private Stock findStockByTicker(String ticker) {
        return stockRepository.findByTicker(ticker)
                .orElseThrow(() -> new ResourceNotFoundException("Stock não encontrado: " + ticker));
    }

    private String normalizeTicker(String ticker) {
        if (ticker == null || ticker.isBlank()) {
            throw new IllegalArgumentException("ticker é obrigatório");
        }
        return ticker.trim().toUpperCase();
    }
}