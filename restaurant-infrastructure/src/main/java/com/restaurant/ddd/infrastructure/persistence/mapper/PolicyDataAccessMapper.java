package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ddd.domain.model.Policy;
import com.restaurant.ddd.infrastructure.persistence.entity.PolicyJpaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class PolicyDataAccessMapper {

    @Autowired
    private ObjectMapper objectMapper;

    public Policy toDomain(PolicyJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Policy policy = new Policy();
        policy.setId(entity.getId());
        
        // Use camelCase getters (Lombok with camelCase fields)
        policy.setName(entity.getName());
        policy.setDescription(entity.getDescription());
        
        // Parse JSON String to List
        try {
            if (entity.getPolicies() != null && !entity.getPolicies().isEmpty()) {
                String[] policies = objectMapper.readValue(entity.getPolicies(), String[].class);
                policy.setPolicies(new ArrayList<>(Arrays.asList(policies)));
            } else {
                policy.setPolicies(new ArrayList<>());
            }
        } catch (JsonProcessingException e) {
            // Log error or handle
            policy.setPolicies(new ArrayList<>());
        }

        // Audit fields
        policy.setCreatedBy(entity.getCreatedBy());
        policy.setUpdatedBy(entity.getUpdatedBy());
        policy.setCreatedDate(entity.getCreatedDate());
        policy.setUpdatedDate(entity.getUpdatedDate());
        policy.setStatus(entity.getStatus());

        return policy;
    }

    public PolicyJpaEntity toEntity(Policy domain) {
        if (domain == null) {
            return null;
        }
        PolicyJpaEntity entity = new PolicyJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        // Use camelCase setters
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        
        // Serialize List to JSON String
        try {
            if (domain.getPolicies() != null) {
                entity.setPolicies(objectMapper.writeValueAsString(domain.getPolicies()));
            } else {
                entity.setPolicies("[]");
            }
        } catch (JsonProcessingException e) {
            entity.setPolicies("[]");
        }

        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setCreatedDate(domain.getCreatedDate());
        entity.setUpdatedDate(domain.getUpdatedDate());
        entity.setStatus(domain.getStatus());
        
        return entity;
    }
}
