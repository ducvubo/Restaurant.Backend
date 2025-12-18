package com.restaurant.ddd.application.model.inventorycount;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BatchInfoDTO {
    private UUID inventoryLedgerId;
    private UUID materialId;
    private String materialName;
    private UUID unitId;
    private String unitName;
    private String batchNumber;
    private LocalDateTime transactionDate;
    private BigDecimal remainingQuantity;    // Số lượng còn lại trong lô
}
