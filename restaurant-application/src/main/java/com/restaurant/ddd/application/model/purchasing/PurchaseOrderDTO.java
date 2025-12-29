package com.restaurant.ddd.application.model.purchasing;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for PurchaseOrder
 */
@Data
public class PurchaseOrderDTO {
    private UUID id;
    private String poCode;
    private UUID rfqId;
    private String rfqCode;
    private UUID supplierId;
    private String supplierName;
    private UUID warehouseId;
    private String warehouseName;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDeliveryDate;
    private String paymentTerms;
    private String deliveryTerms;
    private BigDecimal totalAmount;
    private BigDecimal receivedAmount;
    private BigDecimal remainingAmount;
    private BigDecimal receivingProgress;
    private String notes;
    private Integer status;
    private String statusName;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    
    private List<PurchaseOrderItemDTO> items;
}
