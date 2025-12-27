package com.restaurant.ddd.application.model.workflow;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.WorkflowType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object cho Workflow (Read)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowDTO {
    private UUID id;
    private WorkflowType workflowType;
    private String description;
    private String workflowDiagram;
    private List<String> listPolicy;
    private String version;
    private DataStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private UUID createdBy;
    private UUID updatedBy;
}
