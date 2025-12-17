package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.AdjustmentItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdjustmentItemRepository {
    AdjustmentItem save(AdjustmentItem item);
    Optional<AdjustmentItem> findById(UUID id);
    List<AdjustmentItem> findByAdjustmentTransactionId(UUID transactionId);
    void deleteById(UUID id);
}
