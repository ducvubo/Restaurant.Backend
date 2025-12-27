package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.infrastructure.persistence.entity.WorkflowJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository cho Workflow
 */
@Repository
public interface WorkflowJpaRepository extends JpaRepository<WorkflowJpaEntity, UUID> {

    /**
     * Tìm active workflow theo type
     */
    @Query("SELECT w FROM WorkflowJpaEntity w WHERE w.workflowType = :workflowType AND w.status = 1")
    Optional<WorkflowJpaEntity> findActiveByType(@Param("workflowType") int workflowType);

    /**
     * Tìm workflows chứa một policy ID
     */
    @Query("SELECT w FROM WorkflowJpaEntity w WHERE w.listPolicy LIKE %:policyId%")
    List<WorkflowJpaEntity> findByPolicyId(@Param("policyId") String policyId);

    /**
     * Lấy max version của workflow type
     */
    @Query("SELECT MAX(w.version) FROM WorkflowJpaEntity w WHERE w.workflowType = :workflowType")
    Optional<String> getMaxVersion(@Param("workflowType") int workflowType);
    
    /**
     * Tìm workflows với filters
     */
    @Query("SELECT w FROM WorkflowJpaEntity w WHERE " +
           "(:workflowType IS NULL OR w.workflowType = :workflowType) AND " +
           "(:status IS NULL OR w.status = :status) AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(w.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<WorkflowJpaEntity> findAllWithFilters(
            @Param("workflowType") Integer workflowType,
            @Param("status") Integer status,
            @Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable);
}
