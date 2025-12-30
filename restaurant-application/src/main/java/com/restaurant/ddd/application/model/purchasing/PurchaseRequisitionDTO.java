package com.restaurant.ddd.application.model.purchasing;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for PurchaseRequisition
 */
@Data
public class PurchaseRequisitionDTO {
    private UUID id;
    private String requisitionCode;
    private UUID warehouseId;
    private String warehouseName;
    private UUID requestedBy;
    private String requestedByName;
    private LocalDateTime requestDate;
    private LocalDateTime requiredDate;
    private Integer priority;
    private String priorityName;
    private String notes;
    private Integer status;
    private String statusName;
    private UUID approvedBy;
    private String approvedByName;
    private LocalDateTime approvedDate;
    private String rejectionReason;
    private BigDecimal totalEstimatedAmount;
    
    // Workflow fields
    private UUID workflowId;
    private String workflowStep;
    private String workflowStepName;
    private String requiredPolicies; // Tên các quyền cần có cho bước hiện tại
    
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    
    private List<PurchaseRequisitionItemDTO> items;
}
