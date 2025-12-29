package com.restaurant.ddd.application.model.purchasing;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request model for creating/updating PurchaseOrder
 */
@Data
public class PurchaseOrderRequest {
    private UUID id;
    private UUID rfqId;
    private UUID supplierId;
    private UUID warehouseId;
    private LocalDateTime expectedDeliveryDate;
    private String paymentTerms;
    private String deliveryTerms;
    private String notes;
    private List<PurchaseOrderItemRequest> items;
}
