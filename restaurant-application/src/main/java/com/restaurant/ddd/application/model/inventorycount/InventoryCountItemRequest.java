package com.restaurant.ddd.application.model.inventorycount;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InventoryCountItemRequest {
    private UUID materialId;
    private UUID unitId;
    private UUID inventoryLedgerId;      // Lô hàng cụ thể
    private BigDecimal actualQuantity;   // Số lượng thực tế đếm được
    private String notes;
}
