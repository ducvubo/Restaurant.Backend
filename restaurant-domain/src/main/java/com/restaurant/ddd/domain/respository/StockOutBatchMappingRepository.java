package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.StockOutBatchMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockOutBatchMappingRepository {
    StockOutBatchMapping save(StockOutBatchMapping mapping);
    Optional<StockOutBatchMapping> findById(UUID id);
    List<StockOutBatchMapping> findByStockOutTransactionId(UUID stockOutTransactionId);
    void delete(StockOutBatchMapping mapping);
    List<StockOutBatchMapping> findByStockOutItemId(UUID stockOutItemId);
    void deleteByStockOutItemId(UUID stockOutItemId);
}
