package com.restaurant.ddd.application.mapper;

import com.restaurant.ddd.application.model.workflow.WorkflowDTO;
import com.restaurant.ddd.application.model.workflow.CreateWorkflowRequest;
import com.restaurant.ddd.application.model.workflow.UpdateWorkflowRequest;
import com.restaurant.ddd.domain.model.Workflow;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mapper giữa Workflow Domain Model và DTOs
 */
@Component
public class WorkflowMapper {

    /**
     * Convert Domain Model → DTO
     */
    public static WorkflowDTO toDTO(Workflow domain) {
        if (domain == null) {
            return null;
        }

        return WorkflowDTO.builder()
                .id(domain.getId())
                .workflowType(domain.getWorkflowType())
                .description(domain.getDescription())
                .workflowDiagram(domain.getWorkflowDiagram())
                .listPolicy(domain.getListPolicy())
                .version(domain.getVersion())
                .status(domain.getStatus())
                .createdDate(domain.getCreatedDate())
                .updatedDate(domain.getUpdatedDate())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }

    /**
     * Convert CreateWorkflowRequest → Domain Model
     */
    public static Workflow toDomain(CreateWorkflowRequest request) {
        if (request == null) {
            return null;
        }

        Workflow workflow = new Workflow();
        workflow.setId(UUID.randomUUID());
        workflow.setWorkflowType(request.getWorkflowType());
        workflow.setDescription(request.getDescription());
        workflow.setWorkflowDiagram(request.getWorkflowDiagram());
        workflow.setListPolicy(request.getListPolicy());
        workflow.setVersion("1.0");
        workflow.setStatus(request.getStatus() != null ? request.getStatus() : com.restaurant.ddd.domain.enums.DataStatus.ACTIVE);
        workflow.setCreatedDate(LocalDateTime.now());
        workflow.setUpdatedDate(LocalDateTime.now());

        return workflow;
    }

    /**
     * Convert UpdateWorkflowRequest → Domain Model
     */
    public static Workflow toDomain(UpdateWorkflowRequest request) {
        if (request == null) {
            return null;
        }

        Workflow workflow = new Workflow();
        workflow.setId(request.getId());
        workflow.setWorkflowType(request.getWorkflowType());
        workflow.setDescription(request.getDescription());
        workflow.setWorkflowDiagram(request.getWorkflowDiagram());
        workflow.setListPolicy(request.getListPolicy());
        workflow.setStatus(request.getStatus() != null ? request.getStatus() : com.restaurant.ddd.domain.enums.DataStatus.ACTIVE);
        workflow.setUpdatedDate(LocalDateTime.now());

        return workflow;
    }
}
