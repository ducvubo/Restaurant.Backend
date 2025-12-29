package com.restaurant.ddd.application.model.purchasing;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for RFQ
 */
@Data
public class RfqDTO {
    private UUID id;
    private String rfqCode;
    private UUID requisitionId;
    private String requisitionCode;
    private UUID supplierId;
    private String supplierName;
    private LocalDateTime sentDate;
    private LocalDateTime validUntil;
    private BigDecimal totalAmount;
    private String paymentTerms;
    private String deliveryTerms;
    private String notes;
    private Integer status;
    private String statusName;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    
    private List<RfqItemDTO> items;
}
