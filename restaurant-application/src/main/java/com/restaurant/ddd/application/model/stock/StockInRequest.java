package com.restaurant.ddd.application.model.stock;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request for Stock In - Hỗ trợ nhiều nguyên liệu
 */
@Data
public class StockInRequest {
    private UUID warehouseId;
    private UUID supplierId;
    private LocalDateTime transactionDate;
    private String referenceNumber;
    private String notes;
    private List<StockInItemRequest> items;  // Danh sách nguyên liệu
}
