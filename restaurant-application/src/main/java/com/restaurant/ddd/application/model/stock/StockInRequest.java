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
    private UUID receivedBy;  // Người nhập kho
    private Integer stockInType; // 1=Nhập từ NCC, 2=Chuyển kho nội bộ
    private List<StockInItemRequest> items;  // Danh sách nguyên liệu
}
