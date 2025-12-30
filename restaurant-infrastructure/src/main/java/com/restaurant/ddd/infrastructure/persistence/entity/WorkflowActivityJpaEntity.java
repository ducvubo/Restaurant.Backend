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
 * JPA Entity for WorkflowActivity - Lịch sử thao tác workflow
 * Dùng chung cho tất cả các loại phiếu (PurchaseRequisition, StockIn, StockOut, Adjustment, etc.)
 */
@Entity
@Table(name = "WORKFLOW_ACTIVITIES", indexes = {
    @Index(name = "idx_workflow_activity_ref", columnList = "REFERENCE_ID, WORKFLOW_TYPE"),
    @Index(name = "idx_workflow_activity_type", columnList = "WORKFLOW_TYPE"),
    @Index(name = "idx_workflow_activity_wf_id", columnList = "WORKFLOW_ID")
})
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkflowActivityJpaEntity extends BaseJpaEntity {

    /**
     * ID của phiếu (PurchaseRequisition.id, StockIn.id, etc.)
     */
    @Column(name = "REFERENCE_ID", nullable = false)
    private UUID referenceId;

    /**
     * Loại workflow/phiếu (PURCHASE_REQUEST = 1, STOCK_IN_APPROVAL = 2, etc.)
     */
    @Column(name = "WORKFLOW_TYPE", nullable = false)
    private Integer workflowType;
    
    /**
     * ID của workflow được sử dụng
     */
    @Column(name = "WORKFLOW_ID")
    private UUID workflowId;

    @Column(name = "STEP_ID", length = 100)
    private String stepId;

    @Column(name = "STEP_NAME", length = 255)
    private String stepName;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Column(name = "ACTION_DATE", nullable = false)
    private LocalDateTime actionDate;

    @Column(name = "USER_ID", nullable = false)
    private UUID userId;
}
