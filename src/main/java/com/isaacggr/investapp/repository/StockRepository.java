package com.isaacggr.investapp.repository;

import com.isaacggr.investapp.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {
    Optional<Stock> findByTicker(String ticker);
    boolean existsByTicker(String ticker);
}