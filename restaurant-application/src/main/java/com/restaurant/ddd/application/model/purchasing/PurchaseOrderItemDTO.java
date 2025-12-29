package com.restaurant.ddd.application.model.purchasing;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for PurchaseOrderItem
 */
@Data
public class PurchaseOrderItemDTO {
    private UUID id;
    private UUID poId;
    private UUID materialId;
    private String materialCode;
    private String materialName;
    private BigDecimal quantity;
    private BigDecimal receivedQuantity;
    private BigDecimal remainingQuantity;
    private UUID unitId;
    private String unitName;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private String notes;
    private Boolean isFullyReceived;
}
