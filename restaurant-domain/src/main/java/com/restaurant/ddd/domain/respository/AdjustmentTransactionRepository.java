package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.AdjustmentTransaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdjustmentTransactionRepository {
    AdjustmentTransaction save(AdjustmentTransaction transaction);
    Optional<AdjustmentTransaction> findById(UUID id);
    List<AdjustmentTransaction> findAll();
    void deleteById(UUID id);
}
