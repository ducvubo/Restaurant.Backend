package com.restaurant.ddd.application.mapper;

import com.restaurant.ddd.application.model.user.CreatePolicyRequest;
import com.restaurant.ddd.application.model.user.PolicyDTO;
import com.restaurant.ddd.application.model.user.UpdatePolicyRequest;
import com.restaurant.ddd.domain.model.Policy;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PolicyMapper {

    public static PolicyDTO toDTO(Policy model) {
        if (model == null) {
            return null;
        }
        PolicyDTO dto = new PolicyDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setDescription(model.getDescription());
        dto.setPolicies(model.getPolicies() != null ? model.getPolicies() : new ArrayList<>());
        dto.setStatus(model.getStatus());
        dto.setCreatedBy(model.getCreatedBy());
        dto.setUpdatedBy(model.getUpdatedBy());
        dto.setCreatedDate(model.getCreatedDate());
        dto.setUpdatedDate(model.getUpdatedDate());
        
        // Note: DeletedBy/DeletedDate are not in Policy Domain Model unless we add Soft Delete support explicitly
        // If BaseEntity had them, we might have lost them in the transition if Policy didn't include them.
        // For now, ignoring deletedBy/deletedDate or forcing them null as Policy relies on Repository's hard/soft delete mechanism.
        
        return dto;
    }

    public static Policy toEntity(CreatePolicyRequest request) {
        if (request == null) {
            return null;
        }
        // Using Domain Constructor for Rich Model behavior
        Policy policy = new Policy(
            request.getName(), 
            request.getDescription() != null ? request.getDescription() : "", 
            request.getPolicies() != null ? request.getPolicies() : new ArrayList<>()
        );
        
        if (request.getStatus() != null) {
            policy.setStatus(request.getStatus());
        }
        return policy;
    }

    public static void updateEntity(Policy model, UpdatePolicyRequest request) {
        if (request == null || model == null) {
            return;
        }
        
        // Use Domain Business Method
        model.updateDetails(
            request.getName() != null ? request.getName() : model.getName(),
            request.getDescription() != null ? request.getDescription() : model.getDescription(),
            request.getPolicies() != null ? request.getPolicies() : model.getPolicies()
        );

        if (request.getStatus() != null) {
            model.setStatus(request.getStatus());
        }
        model.setUpdatedDate(LocalDateTime.now());
    }
}
