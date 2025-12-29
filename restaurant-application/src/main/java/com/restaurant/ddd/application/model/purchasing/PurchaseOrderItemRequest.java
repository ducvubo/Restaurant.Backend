package com.restaurant.ddd.application.model.purchasing;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request model for PurchaseOrderItem
 */
@Data
public class PurchaseOrderItemRequest {
    private UUID id;
    private UUID materialId;
    private BigDecimal quantity;
    private UUID unitId;
    private BigDecimal unitPrice;
    private String notes;
}
