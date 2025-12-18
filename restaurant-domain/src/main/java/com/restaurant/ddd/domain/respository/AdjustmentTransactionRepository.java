package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.AdjustmentTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdjustmentTransactionRepository {
    AdjustmentTransaction save(AdjustmentTransaction transaction);
    Optional<AdjustmentTransaction> findById(UUID id);
    List<AdjustmentTransaction> findAll();
    Page<AdjustmentTransaction> findAll(UUID warehouseId, UUID materialId, LocalDateTime fromDate, LocalDateTime toDate, Integer status, Pageable pageable);
    void deleteById(UUID id);
}
