package com.restaurant.ddd.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * AdjustmentBatchMapping - Mapping giữa adjustment item và inventory ledger batch
 * Dùng cho điều chỉnh GIẢM (FIFO)
 */
@Data
@Accessors(chain = true)
public class AdjustmentBatchMapping {
    private UUID id;
    private UUID adjustmentItemId;
    private UUID inventoryLedgerId;  // Batch được sử dụng
    private BigDecimal quantityUsed;
}
