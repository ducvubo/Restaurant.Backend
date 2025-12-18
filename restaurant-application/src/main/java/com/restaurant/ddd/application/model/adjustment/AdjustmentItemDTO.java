package com.restaurant.ddd.application.model.adjustment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AdjustmentItemDTO {
    private UUID id;
    private UUID materialId;
    private String materialName;
    private UUID unitId;
    private String unitName;
    private BigDecimal quantity;
    private UUID inventoryLedgerId; // For batch tracking
    private String batchNumber; // Batch number or transaction code
    private String notes;
}
