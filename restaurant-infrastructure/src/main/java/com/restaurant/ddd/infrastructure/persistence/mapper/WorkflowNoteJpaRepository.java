package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.infrastructure.persistence.entity.WorkflowNoteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository for WorkflowNote
 * Dùng chung cho tất cả loại phiếu
 */
@Repository
public interface WorkflowNoteJpaRepository extends JpaRepository<WorkflowNoteJpaEntity, UUID> {
    
    /**
     * Tìm tất cả notes theo referenceId và workflowType
     */
    List<WorkflowNoteJpaEntity> findByReferenceIdAndWorkflowTypeOrderByCreatedDateAsc(
            UUID referenceId, Integer workflowType);
    
    /**
     * Tìm notes theo referenceId, workflowType và stepId
     */
    List<WorkflowNoteJpaEntity> findByReferenceIdAndWorkflowTypeAndStepId(
            UUID referenceId, Integer workflowType, String stepId);
}
