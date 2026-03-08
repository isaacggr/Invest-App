package com.isaacggr.investapp.service;

import com.isaacggr.investapp.dto.position.PositionResponse;
import com.isaacggr.investapp.entity.Transaction;
import com.isaacggr.investapp.enums.OperationType;
import com.isaacggr.investapp.exception.BusinessRuleException;
import com.isaacggr.investapp.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private PositionService positionService;

    @Test
    void shouldCalculatePositionWithOnlyBuys() {
        UUID accountId = UUID.randomUUID();
        UUID stockId = UUID.randomUUID();

        Transaction buy1 = mockTransaction(
                OperationType.BUY,
                "10",
                "20.00",
                "2.00"
        );

        Transaction buy2 = mockTransaction(
                OperationType.BUY,
                "5",
                "30.00",
                "1.00"
        );

        when(transactionRepository.findByAccount_IdAndStock_IdOrderByTradeDateAscCreatedAtAsc(accountId, stockId))
                .thenReturn(List.of(buy1, buy2));

        PositionResponse response = positionService.calculatePosition(accountId, stockId, "petr4");

        assertNotNull(response);
        assertEquals(stockId, response.stockId());
        assertEquals("PETR4", response.ticker());
        assertTrue(response.quantity().compareTo(new BigDecimal("15")) == 0);
        assertTrue(response.totalCost().compareTo(new BigDecimal("353.00")) == 0);
        assertTrue(response.avgPrice().compareTo(new BigDecimal("23.533333")) == 0);
    }

    @Test
    void shouldCalculatePositionAfterSell() {
        UUID accountId = UUID.randomUUID();
        UUID stockId = UUID.randomUUID();

        Transaction buy1 = mockTransaction(
                OperationType.BUY,
                "10",
                "20.00",
                "0.00"
        );

        Transaction buy2 = mockTransaction(
                OperationType.BUY,
                "10",
                "30.00",
                "0.00"
        );

        Transaction sell = mockTransaction(
                OperationType.SELL,
                "5",
                "35.00",
                "0.00"
        );

        when(transactionRepository.findByAccount_IdAndStock_IdOrderByTradeDateAscCreatedAtAsc(accountId, stockId))
                .thenReturn(List.of(buy1, buy2, sell));

        PositionResponse response = positionService.calculatePosition(accountId, stockId, "vale3");

        assertNotNull(response);
        assertEquals("VALE3", response.ticker());
        assertTrue(response.quantity().compareTo(new BigDecimal("15")) == 0);
        assertTrue(response.avgPrice().compareTo(new BigDecimal("25.000000")) == 0);
        assertTrue(response.totalCost().compareTo(new BigDecimal("375.000000")) == 0);
    }

    @Test
    void shouldThrowBusinessRuleExceptionWhenSellQuantityIsGreaterThanPosition() {
        UUID accountId = UUID.randomUUID();
        UUID stockId = UUID.randomUUID();

        Transaction buy = mockTransaction(
                OperationType.BUY,
                "10",
                "20.00",
                "0.00"
        );

        Transaction invalidSell = mockTransaction(
                OperationType.SELL,
                "15",
                "22.00",
                "0.00"
        );

        when(transactionRepository.findByAccount_IdAndStock_IdOrderByTradeDateAscCreatedAtAsc(accountId, stockId))
                .thenReturn(List.of(buy, invalidSell));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> positionService.calculatePosition(accountId, stockId, "ITSA4")
        );

        assertTrue(exception.getMessage().contains("Venda inválida"));
    }

    @Test
    void shouldReturnCurrentQuantityCorrectly() {
        UUID accountId = UUID.randomUUID();
        UUID stockId = UUID.randomUUID();

        Transaction buy1 = mockQuantityTransaction(
                OperationType.BUY,
                "10"
        );

        Transaction buy2 = mockQuantityTransaction(
                OperationType.BUY,
                "5"
        );

        Transaction sell = mockQuantityTransaction(
                OperationType.SELL,
                "4"
        );

        when(transactionRepository.findByAccount_IdAndStock_IdOrderByTradeDateAscCreatedAtAsc(accountId, stockId))
                .thenReturn(List.of(buy1, buy2, sell));

        BigDecimal quantity = positionService.getCurrentQuantity(accountId, stockId);

        assertTrue(quantity.compareTo(new BigDecimal("11")) == 0);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenAccountIdIsNull() {
        UUID stockId = UUID.randomUUID();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> positionService.calculatePosition(null, stockId, "PETR4")
        );

        assertEquals("accountId é obrigatório", exception.getMessage());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenStockIdIsNull() {
        UUID accountId = UUID.randomUUID();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> positionService.calculatePosition(accountId, null, "PETR4")
        );

        assertEquals("stockId é obrigatório", exception.getMessage());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenTickerIsBlank() {
        UUID accountId = UUID.randomUUID();
        UUID stockId = UUID.randomUUID();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> positionService.calculatePosition(accountId, stockId, " ")
        );

        assertEquals("ticker é obrigatório", exception.getMessage());
    }

    private Transaction mockTransaction(
            OperationType operation,
            String quantity,
            String unitPrice,
            String fees
    ) {
        Transaction transaction = mock(Transaction.class);

        when(transaction.getOperation()).thenReturn(operation);
        when(transaction.getQuantity()).thenReturn(new BigDecimal(quantity));
        when(transaction.getUnitPrice()).thenReturn(new BigDecimal(unitPrice));
        when(transaction.getFees()).thenReturn(new BigDecimal(fees));

        return transaction;
    }

    private Transaction mockQuantityTransaction(
            OperationType operation,
            String quantity
    ) {
        Transaction transaction = mock(Transaction.class);

        when(transaction.getOperation()).thenReturn(operation);
        when(transaction.getQuantity()).thenReturn(new BigDecimal(quantity));

        return transaction;
    }
}