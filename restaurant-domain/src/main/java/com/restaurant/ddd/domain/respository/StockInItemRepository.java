package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.StockInItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockInItemRepository {
    StockInItem save(StockInItem item);
    Optional<StockInItem> findById(UUID id);
    List<StockInItem> findByStockInTransactionId(UUID stockInTransactionId);
    void deleteByStockInTransactionId(UUID stockInTransactionId);
}
