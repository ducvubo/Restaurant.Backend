package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.workflow.*;
import com.restaurant.ddd.domain.enums.WorkflowType;
import com.restaurant.ddd.domain.model.ResultMessage;

import java.util.List;
import java.util.UUID;

/**
 * Application Service Interface cho Workflow
 * Định nghĩa các use cases
 */
public interface WorkflowAppService {
    
    // ===== Create =====
    ResultMessage<WorkflowDTO> create(CreateWorkflowRequest request, UUID userId, boolean isForceActive);
    
    // ===== Update =====
    ResultMessage<WorkflowDTO> update(UUID id, UpdateWorkflowRequest request, UUID userId, boolean isForceActive);
    
    // ===== Delete =====
    ResultMessage<String> delete(UUID id, UUID userId);
    
    // ===== Get =====
    ResultMessage<WorkflowDTO> getById(UUID id);
    
    // ===== Get List =====
    ResultMessage<WorkflowListResponse> getList(WorkflowListRequest request);
    
    // ===== Get Active by Type =====
    ResultMessage<WorkflowDTO> getActiveByType(WorkflowType workflowType);
    
    // ===== BPMN Validation =====
    BpmnValidationResult validateBpmn(String bpmnXml);
    
    // ===== Extract Policy IDs from BPMN =====
    List<String> extractPolicyIdsFromBpmn(String bpmnXml);
    
    // ===== Activate/Deactivate =====
    ResultMessage<String> activate(UUID id, UUID userId, boolean isForceActive);
    
    ResultMessage<String> deactivate(UUID id, UUID userId);
}
