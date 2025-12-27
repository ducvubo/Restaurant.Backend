package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.enums.WorkflowType;
import com.restaurant.ddd.domain.model.Workflow;
import com.restaurant.ddd.domain.respository.WorkflowRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.WorkflowJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.WorkflowDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.mapper.WorkflowJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository Implementation cho Workflow
 * Implement WorkflowRepository interface từ Domain Layer
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class WorkflowRepositoryImpl implements WorkflowRepository {

    private final WorkflowJpaRepository workflowJpaRepository;
    private final WorkflowDataAccessMapper dataAccessMapper;

    @Override
    @Transactional
    public Workflow save(Workflow workflow) {
        log.info("WorkflowRepository: Saving workflow - type: {}", 
                workflow.getWorkflowType());
        
        WorkflowJpaEntity entity = WorkflowDataAccessMapper.toJpaEntity(workflow);
        WorkflowJpaEntity savedEntity = workflowJpaRepository.save(entity);
        
        return WorkflowDataAccessMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Workflow> findById(UUID id) {
        log.debug("WorkflowRepository: Finding workflow by id - {}", id);
        
        return workflowJpaRepository.findById(id)
                .map(WorkflowDataAccessMapper::toDomain);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("WorkflowRepository: Deleting workflow - {}", id);
        workflowJpaRepository.deleteById(id);
    }

    @Override
    public Page<Workflow> findAll(Pageable pageable) {
        log.debug("WorkflowRepository: Finding all workflows with pagination");
        
        Page<WorkflowJpaEntity> entityPage = workflowJpaRepository.findAll(pageable);
        List<Workflow> workflows = entityPage.getContent().stream()
                .map(WorkflowDataAccessMapper::toDomain)
                .collect(Collectors.toList());
        
        return new PageImpl<>(workflows, pageable, entityPage.getTotalElements());
    }

    @Override
    public Optional<Workflow> findActiveByType(WorkflowType workflowType) {
        log.debug("WorkflowRepository: Finding active workflow by type - {}", workflowType);
        
        return workflowJpaRepository.findActiveByType(workflowType.code())
                .map(WorkflowDataAccessMapper::toDomain);
    }

    @Override
    public List<Workflow> findByPolicyId(String policyId) {
        log.debug("WorkflowRepository: Finding workflows by policy - {}", policyId);
        
        return workflowJpaRepository.findByPolicyId(policyId).stream()
                .map(WorkflowDataAccessMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public String getMaxVersion(WorkflowType workflowType) {
        log.debug("WorkflowRepository: Getting max version - type: {}", 
                workflowType);
        
        return workflowJpaRepository.getMaxVersion(workflowType.code())
                .orElse("0.0");
    }

    @Override
    @Transactional
    public void saveAll(List<Workflow> workflows) {
        log.info("WorkflowRepository: Saving {} workflows", workflows.size());
        
        List<WorkflowJpaEntity> entities = workflows.stream()
                .map(WorkflowDataAccessMapper::toJpaEntity)
                .collect(Collectors.toList());
        
        workflowJpaRepository.saveAll(entities);
    }

    @Override
    public Page<Workflow> findAllWithFilters(Integer workflowType, Integer status, String keyword, Pageable pageable) {
        log.debug("WorkflowRepository: Finding workflows with filters - type: {}, status: {}, keyword: {}", 
                workflowType, status, keyword);
        
        Page<WorkflowJpaEntity> entityPage = workflowJpaRepository.findAllWithFilters(
                workflowType, status, keyword, pageable);
        
        List<Workflow> workflows = entityPage.getContent().stream()
                .map(WorkflowDataAccessMapper::toDomain)
                .collect(Collectors.toList());
        
        return new PageImpl<>(workflows, pageable, entityPage.getTotalElements());
    }
}
