package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.common.PageResponse;
import com.restaurant.ddd.application.model.purchasing.*;
import com.restaurant.ddd.application.model.workflow.*;

import java.util.List;
import java.util.UUID;

/**
 * Application Service interface for PurchaseRequisition
 */
public interface PurchaseRequisitionAppService {
    
    /**
     * Create new purchase requisition
     */
    PurchaseRequisitionDTO create(PurchaseRequisitionRequest request);
    
    /**
     * Get purchase requisition by ID
     */
    PurchaseRequisitionDTO getById(UUID id);
    
    /**
     * Get list with pagination and filters
     */
    PageResponse<PurchaseRequisitionDTO> getList(PurchaseListRequest request);
    
    /**
     * Update purchase requisition
     */
    PurchaseRequisitionDTO update(UUID id, PurchaseRequisitionRequest request);
    
    /**
     * Submit for approval
     */
    PurchaseRequisitionDTO submit(UUID id);
    
    /**
     * Approve requisition
     */
    PurchaseRequisitionDTO approve(UUID id);
    
    /**
     * Reject requisition
     */
    PurchaseRequisitionDTO reject(UUID id, String reason);
    
    /**
     * Cancel requisition
     */
    PurchaseRequisitionDTO cancel(UUID id);
    
    /**
     * Delete requisition (only draft)
     */
    void delete(UUID id);
    
    // ===== Workflow Methods =====
    
    /**
     * Lấy trạng thái workflow hiện tại
     */
    WorkflowStateDTO getWorkflowState(UUID id);
    
    /**
     * Thực hiện action trong workflow (chuyển bước)
     */
    PurchaseRequisitionDTO performWorkflowAction(UUID id, WorkflowActionRequest request);
    
    /**
     * Lấy lịch sử thao tác workflow
     */
    List<WorkflowActivityDTO> getHistory(UUID id);
}

