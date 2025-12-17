package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.StockOutItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockOutItemRepository {
    StockOutItem save(StockOutItem item);
    Optional<StockOutItem> findById(UUID id);
    List<StockOutItem> findByStockOutTransactionId(UUID stockOutTransactionId);
    void deleteByStockOutTransactionId(UUID stockOutTransactionId);
}
