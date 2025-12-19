package com.restaurant.ddd.application.model.adjustment;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AdjustmentTransactionDTO {
    private UUID id;
    private String transactionCode;
    private UUID warehouseId;
    private String warehouseName;
    private Integer adjustmentType;      // 1=Tăng, 2=Giảm
    private String adjustmentTypeName;
    private LocalDateTime transactionDate;
    private String reason;
    private String referenceNumber;
    private String notes;
    private Boolean isLocked;
    private Integer status;
    private UUID performedBy;
    private String performedByName;
    private UUID createdBy;
    private String createdByName;
    private LocalDateTime createdDate;
    
    // Items
    private List<AdjustmentItemDTO> items;
}
