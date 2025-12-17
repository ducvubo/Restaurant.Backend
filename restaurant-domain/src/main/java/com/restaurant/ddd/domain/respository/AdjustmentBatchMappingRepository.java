package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.AdjustmentBatchMapping;

import java.util.List;
import java.util.UUID;

public interface AdjustmentBatchMappingRepository {
    AdjustmentBatchMapping save(AdjustmentBatchMapping mapping);
    List<AdjustmentBatchMapping> findByAdjustmentItemId(UUID itemId);
    void delete(AdjustmentBatchMapping mapping);
}
