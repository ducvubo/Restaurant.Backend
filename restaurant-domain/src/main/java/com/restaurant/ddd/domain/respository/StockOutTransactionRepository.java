package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.StockOutTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockOutTransactionRepository {
    StockOutTransaction save(StockOutTransaction transaction);
    Optional<StockOutTransaction> findById(UUID id);
    List<StockOutTransaction> findAll();
    
    /**
     * Find all stock out transactions with filters and pagination
     */
    Page<StockOutTransaction> findAll(
        UUID warehouseId,
        UUID materialId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );
}
