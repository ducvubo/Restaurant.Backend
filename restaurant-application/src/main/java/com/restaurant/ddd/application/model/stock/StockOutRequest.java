package com.restaurant.ddd.application.model.stock;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request for Stock Out - Hỗ trợ nhiều nguyên liệu
 */
@Data
public class StockOutRequest {
    private UUID warehouseId;
    private UUID destinationBranchId;
    private LocalDateTime transactionDate;
    private String referenceNumber;
    private String notes;
    private List<StockOutItemRequest> items;  // Danh sách nguyên liệu
    
    // Stock Out Type specific fields
    private Integer stockOutType;              // Required: 1=Transfer, 2=Sale, 3=Disposal
    private UUID destinationWarehouseId;       // For INTERNAL_TRANSFER
    private UUID customerId;                   // For RETAIL_SALE
    private String disposalReason;             // For DISPOSAL
}
