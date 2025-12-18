package com.restaurant.ddd.application.model.inventorycount;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InventoryCountItemDTO {
    private UUID id;
    private UUID inventoryCountId;
    private UUID materialId;
    private String materialName;
    private UUID unitId;
    private String unitName;
    private UUID inventoryLedgerId;
    private String batchNumber;
    private LocalDateTime transactionDate;
    private BigDecimal systemQuantity;
    private BigDecimal actualQuantity;
    private BigDecimal differenceQuantity;
    private String notes;
    private LocalDateTime createdDate;
}
