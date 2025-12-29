package com.restaurant.ddd.application.model.purchasing;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request model for creating/updating RFQ
 */
@Data
public class RfqRequest {
    private UUID id;
    private UUID requisitionId;
    private UUID supplierId;
    private LocalDateTime validUntil;
    private String paymentTerms;
    private String deliveryTerms;
    private String notes;
    private List<RfqItemRequest> items;
}
