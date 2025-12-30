package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for PurchaseRequisition - Yêu cầu mua hàng
 */
@Entity
@Table(name = "PURCHASE_REQUISITIONS")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PurchaseRequisitionJpaEntity extends BaseJpaEntity {

    @Column(name = "REQUISITION_CODE", nullable = false, unique = true, length = 50)
    private String requisitionCode;

    @Column(name = "WAREHOUSE_ID", nullable = false)
    private UUID warehouseId;

    @Column(name = "REQUESTED_BY")
    private UUID requestedBy;

    @Column(name = "REQUEST_DATE", nullable = false)
    private LocalDateTime requestDate;

    @Column(name = "REQUIRED_DATE")
    private LocalDateTime requiredDate;

    @Column(name = "PRIORITY")
    private Integer priority;

    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "APPROVED_BY")
    private UUID approvedBy;

    @Column(name = "APPROVED_DATE")
    private LocalDateTime approvedDate;

    @Column(name = "REJECTION_REASON", columnDefinition = "TEXT")
    private String rejectionReason;
    
    // Trạng thái yêu cầu (DRAFT=1, PENDING=2, APPROVED=3, REJECTED=4, CONVERTED=5, CANCELLED=-1)
    @Column(name = "REQUISITION_STATUS")
    private Integer requisitionStatus;
    
    // Workflow fields
    @Column(name = "WORKFLOW_ID")
    private UUID workflowId;
    
    @Column(name = "WORKFLOW_STEP", length = 100)
    private String workflowStep;
}
