package com.restaurant.ddd.application.model.purchasing;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for PurchaseRequisitionItem
 */
@Data
public class PurchaseRequisitionItemDTO {
    private UUID id;
    private UUID requisitionId;
    private UUID materialId;
    private String materialCode;
    private String materialName;
    private BigDecimal quantity;
    private UUID unitId;
    private String unitName;
    private BigDecimal estimatedPrice;
    private BigDecimal estimatedAmount;
    private String notes;
}
