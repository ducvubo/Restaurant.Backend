package com.restaurant.ddd.application.model.purchasing;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request model for creating/updating PurchaseRequisition
 */
@Data
public class PurchaseRequisitionRequest {
    private UUID id;
    private UUID warehouseId;
    private LocalDateTime requiredDate;
    private Integer priority;
    private String notes;
    private List<PurchaseRequisitionItemRequest> items;
}
