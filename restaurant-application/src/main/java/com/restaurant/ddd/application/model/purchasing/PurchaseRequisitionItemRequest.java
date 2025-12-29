package com.restaurant.ddd.application.model.purchasing;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request model for PurchaseRequisitionItem
 */
@Data
public class PurchaseRequisitionItemRequest {
    private UUID id;
    private UUID materialId;
    private BigDecimal quantity;
    private UUID unitId;
    private BigDecimal estimatedPrice;
    private String notes;
}
