package com.restaurant.ddd.application.model.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO cho Workflow Note
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowNoteDTO {
    
    private UUID id;
    private UUID referenceId;
    private Integer workflowType;
    private String stepId;
    private String stepName;
    private String content;
    private UUID userId;
    private String userName;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private boolean canEdit;    // Có thể sửa (chỉ owner)
    private boolean canDelete;  // Có thể xóa (chỉ owner)
}
