package com.restaurant.ddd.application.model.stock;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request for listing stock transactions
 */
@Data
public class StockTransactionListRequest {
    private Integer page;
    private Integer size;
    private UUID warehouseId;
    private UUID materialId;
    private Integer transactionType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
