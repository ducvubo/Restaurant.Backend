package com.restaurant.ddd.application.model.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for StockOutBatchMapping
 */
@Data
public class StockOutBatchMappingDTO {
    private UUID id;
    private UUID inventoryLedgerId;
    private BigDecimal quantityUsed;
    private BigDecimal unitPrice;
    private String batchNumber;  // Batch number from inventory ledger
}
