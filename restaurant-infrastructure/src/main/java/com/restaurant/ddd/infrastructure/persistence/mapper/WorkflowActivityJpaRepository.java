package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.infrastructure.persistence.entity.WorkflowActivityJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository for WorkflowActivity
 * Dùng chung cho tất cả loại phiếu
 */
@Repository
public interface WorkflowActivityJpaRepository extends JpaRepository<WorkflowActivityJpaEntity, UUID> {
    
    /**
     * Tìm tất cả activities theo referenceId và workflowType, sắp xếp theo thời gian
     */
    List<WorkflowActivityJpaEntity> findByReferenceIdAndWorkflowTypeOrderByActionDateAsc(
            UUID referenceId, Integer workflowType);
    
    /**
     * Tìm tất cả activities theo referenceId, không phân biệt loại
     */
    List<WorkflowActivityJpaEntity> findByReferenceIdOrderByActionDateAsc(UUID referenceId);
    
    /**
     * Xóa tất cả activities theo referenceId
     */
    void deleteByReferenceId(UUID referenceId);
    
    /**
     * Kiểm tra workflow đã được sử dụng chưa (có activity nào tham chiếu đến workflow này không)
     */
    boolean existsByWorkflowId(UUID workflowId);
    
    /**
     * Đếm số lượng activities sử dụng workflow
     */
    long countByWorkflowId(UUID workflowId);
}
