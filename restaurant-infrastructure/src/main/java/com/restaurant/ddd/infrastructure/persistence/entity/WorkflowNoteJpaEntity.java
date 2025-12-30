package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;

/**
 * JPA Entity for WorkflowNote - Ghi chú cho workflow
 * Dùng chung cho tất cả các loại phiếu (PurchaseRequisition, StockIn, StockOut, Adjustment, etc.)
 */
@Entity
@Table(name = "WORKFLOW_NOTES", indexes = {
    @Index(name = "idx_workflow_note_ref", columnList = "REFERENCE_ID, WORKFLOW_TYPE"),
    @Index(name = "idx_workflow_note_type", columnList = "WORKFLOW_TYPE")
})
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkflowNoteJpaEntity extends BaseJpaEntity {

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

    // Null/empty = ghi chú chung; có STEP_ID = ghi chú gắn với một bước
    @Column(name = "STEP_ID", length = 100)
    private String stepId;

    @Column(name = "STEP_NAME", length = 255)
    private String stepName;

    @Column(name = "CONTENT", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "USER_ID", nullable = false)
    private UUID userId;
}
