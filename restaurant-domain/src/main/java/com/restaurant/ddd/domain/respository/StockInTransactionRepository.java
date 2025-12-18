package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.StockInTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockInTransactionRepository {
    StockInTransaction save(StockInTransaction transaction);
    Optional<StockInTransaction> findById(UUID id);
    List<StockInTransaction> findAll();
    
    /**
     * Find all stock in transactions with filters and pagination
     */
    Page<StockInTransaction> findAll(
        UUID warehouseId,
        UUID materialId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );
}
