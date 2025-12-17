package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.StockInTransaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockInTransactionRepository {
    StockInTransaction save(StockInTransaction transaction);
    Optional<StockInTransaction> findById(UUID id);
    List<StockInTransaction> findAll();
}
