package com.restaurant.ddd.application.model.stock;

import com.restaurant.ddd.application.model.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request for listing stock transactions
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StockTransactionListRequest extends PageRequest {
    private UUID warehouseId;
    private UUID materialId;
    private Integer transactionType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
