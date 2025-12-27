package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.WorkflowType;
import com.restaurant.ddd.domain.model.Workflow;
import com.restaurant.ddd.infrastructure.persistence.entity.WorkflowJpaEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper giữa Domain Model (Workflow) và JPA Entity (WorkflowJpaEntity)
 */
@Component
public class WorkflowDataAccessMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Convert JPA Entity → Domain Model
     */
    public static Workflow toDomain(WorkflowJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Workflow workflow = new Workflow();
        workflow.setId(entity.getId());
        workflow.setWorkflowType(WorkflowType.fromCode(entity.getWorkflowType()));
        workflow.setDescription(entity.getDescription());
        workflow.setWorkflowDiagram(entity.getWorkflowDiagram());
        workflow.setVersion(entity.getVersion());
        workflow.setStatus(entity.getStatus());
        workflow.setCreatedDate(entity.getCreatedDate());
        workflow.setUpdatedDate(entity.getUpdatedDate());
        workflow.setCreatedBy(entity.getCreatedBy());
        workflow.setUpdatedBy(entity.getUpdatedBy());

        // Parse JSON list of policies
        List<String> policies = parseListPolicy(entity.getListPolicy());
        workflow.setListPolicy(policies);

        return workflow;
    }

    /**
     * Convert Domain Model → JPA Entity
     */
    public static WorkflowJpaEntity toJpaEntity(Workflow domain) {
        if (domain == null) {
            return null;
        }

        WorkflowJpaEntity entity = new WorkflowJpaEntity();
        entity.setId(domain.getId());
        entity.setWorkflowType(domain.getWorkflowType().code());
        entity.setDescription(domain.getDescription());
        entity.setWorkflowDiagram(domain.getWorkflowDiagram());
        entity.setVersion(domain.getVersion());
        entity.setStatus(domain.getStatus());
        entity.setCreatedDate(domain.getCreatedDate());
        entity.setUpdatedDate(domain.getUpdatedDate());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedBy(domain.getUpdatedBy());

        // Convert list of policies to JSON string
        String policiesJson = convertListPolicyToJson(domain.getListPolicy());
        entity.setListPolicy(policiesJson);

        return entity;
    }

    /**
     * Parse JSON string to List<String> of policy IDs
     */
    private static List<String> parseListPolicy(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(jsonString, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Convert List<String> of policy IDs to JSON string
     */
    private static String convertListPolicyToJson(List<String> policies) {
        if (policies == null || policies.isEmpty()) {
            return "[]";
        }

        try {
            return objectMapper.writeValueAsString(policies);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
