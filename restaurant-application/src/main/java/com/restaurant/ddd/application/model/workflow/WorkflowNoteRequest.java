package com.restaurant.ddd.application.model.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request để tạo/cập nhật Workflow Note
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowNoteRequest {
    
    private UUID referenceId;      // ID của phiếu
    private Integer workflowType;  // Loại phiếu
    private String stepId;         // Bước workflow (tùy chọn)
    private String stepName;       // Tên bước (tùy chọn)
    private String content;        // Nội dung ghi chú
}
