package com.isaacggr.investapp.repository;

import com.isaacggr.investapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByAccount_IdAndStock_IdOrderByTradeDateAscCreatedAtAsc(UUID accountId, UUID stockId);

    List<Transaction> findByAccount_IdOrderByTradeDateAscCreatedAtAsc(UUID accountId);
}