package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.StockOutTransaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockOutTransactionRepository {
    StockOutTransaction save(StockOutTransaction transaction);
    Optional<StockOutTransaction> findById(UUID id);
    List<StockOutTransaction> findAll();
}
