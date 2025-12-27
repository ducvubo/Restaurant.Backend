package com.restaurant.ddd.application.model.workflow;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.WorkflowType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO để cập nhật Workflow
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateWorkflowRequest {
    private UUID id;
    private WorkflowType workflowType;
    private String description;
    private String workflowDiagram;
    private List<String> listPolicy;
    private DataStatus status;
}
