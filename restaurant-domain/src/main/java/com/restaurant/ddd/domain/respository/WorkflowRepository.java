package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.enums.WorkflowType;
import com.restaurant.ddd.domain.model.Workflow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Interface cho Workflow
 * Định nghĩa các phương thức truy cập data
 * 
 * Nằm trong Domain Layer - Infrastructure implement interface này
 */
public interface WorkflowRepository {
    // ===== Basic CRUD =====
    Workflow save(Workflow workflow);
    Optional<Workflow> findById(UUID id);
    void delete(UUID id);
    Page<Workflow> findAll(Pageable pageable);
    
    // ===== Query Methods =====
    /**
     * Lấy active workflow theo type
     * Chỉ có 1 active workflow per type
     */
    Optional<Workflow> findActiveByType(WorkflowType workflowType);
    
    /**
     * Tìm workflows chứa một policy ID nhất định
     */
    List<Workflow> findByPolicyId(String policyId);
    
    /**
     * Lấy max version của workflow type
     */
    String getMaxVersion(WorkflowType workflowType);
    
    /**
     * Update multiple workflows
     */
    void saveAll(List<Workflow> workflows);
    
    /**
     * Tìm workflows với filters
     */
    Page<Workflow> findAllWithFilters(Integer workflowType, Integer status, String keyword, Pageable pageable);
}
