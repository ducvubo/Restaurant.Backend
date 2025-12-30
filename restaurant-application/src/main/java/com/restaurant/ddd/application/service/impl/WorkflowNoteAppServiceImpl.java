package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.model.workflow.WorkflowNoteDTO;
import com.restaurant.ddd.application.model.workflow.WorkflowNoteRequest;
import com.restaurant.ddd.application.service.WorkflowNoteAppService;
import com.restaurant.ddd.infrastructure.persistence.entity.UserManagementJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.entity.WorkflowNoteJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.UserJpaMapper;
import com.restaurant.ddd.infrastructure.persistence.mapper.WorkflowNoteJpaRepository;
import com.restaurant.ddd.infrastructure.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation của WorkflowNoteAppService
 */
@Service
@Slf4j
public class WorkflowNoteAppServiceImpl implements WorkflowNoteAppService {

    @Autowired
    private WorkflowNoteJpaRepository noteRepository;
    
    @Autowired
    private UserJpaMapper userJpaMapper;

    @Override
    public List<WorkflowNoteDTO> getNotes(UUID referenceId, Integer workflowType) {
        List<WorkflowNoteJpaEntity> notes = noteRepository
                .findByReferenceIdAndWorkflowTypeOrderByCreatedDateAsc(referenceId, workflowType);
        
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        
        // Batch load user names
        List<UUID> userIds = notes.stream()
                .map(WorkflowNoteJpaEntity::getUserId)
                .filter(uid -> uid != null)
                .distinct()
                .collect(Collectors.toList());
        
        Map<UUID, String> userNames = userIds.stream()
                .map(userJpaMapper::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(UserManagementJpaEntity::getId, UserManagementJpaEntity::getFullName));
        
        return notes.stream()
                .map(entity -> toDTO(entity, currentUserId, userNames.get(entity.getUserId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WorkflowNoteDTO createNote(WorkflowNoteRequest request) {
        log.info("Creating workflow note for reference {} type {}", request.getReferenceId(), request.getWorkflowType());
        
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        
        WorkflowNoteJpaEntity entity = new WorkflowNoteJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setReferenceId(request.getReferenceId());
        entity.setWorkflowType(request.getWorkflowType());
        entity.setStepId(request.getStepId());
        entity.setStepName(request.getStepName());
        entity.setContent(request.getContent());
        entity.setUserId(currentUserId);
        entity.setCreatedBy(currentUserId);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setUpdatedBy(currentUserId);
        entity.setUpdatedDate(LocalDateTime.now());
        
        WorkflowNoteJpaEntity saved = noteRepository.save(entity);
        
        // Get current user name
        String userName = userJpaMapper.findById(currentUserId)
                .map(UserManagementJpaEntity::getFullName)
                .orElse(null);
        
        return toDTO(saved, currentUserId, userName);
    }

    @Override
    @Transactional
    public WorkflowNoteDTO updateNote(UUID noteId, WorkflowNoteRequest request) {
        log.info("Updating workflow note {}", noteId);
        
        WorkflowNoteJpaEntity entity = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ghi chú với id: " + noteId));
        
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        
        // Kiểm tra ownership
        if (!entity.getUserId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền sửa ghi chú này");
        }
        
        entity.setStepId(request.getStepId());
        entity.setStepName(request.getStepName());
        entity.setContent(request.getContent());
        entity.setUpdatedBy(currentUserId);
        entity.setUpdatedDate(LocalDateTime.now());
        
        WorkflowNoteJpaEntity saved = noteRepository.save(entity);
        
        // Get current user name
        String userName = userJpaMapper.findById(currentUserId)
                .map(UserManagementJpaEntity::getFullName)
                .orElse(null);
        
        return toDTO(saved, currentUserId, userName);
    }

    @Override
    @Transactional
    public void deleteNote(UUID noteId) {
        log.info("Deleting workflow note {}", noteId);
        
        WorkflowNoteJpaEntity entity = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ghi chú với id: " + noteId));
        
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        
        // Kiểm tra ownership
        if (!entity.getUserId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền xóa ghi chú này");
        }
        
        noteRepository.deleteById(noteId);
    }
    
    private WorkflowNoteDTO toDTO(WorkflowNoteJpaEntity entity, UUID currentUserId, String userName) {
        boolean isOwner = entity.getUserId().equals(currentUserId);
        
        return WorkflowNoteDTO.builder()
                .id(entity.getId())
                .referenceId(entity.getReferenceId())
                .workflowType(entity.getWorkflowType())
                .stepId(entity.getStepId())
                .stepName(entity.getStepName())
                .content(entity.getContent())
                .userId(entity.getUserId())
                .userName(userName)
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .canEdit(isOwner)
                .canDelete(isOwner)
                .build();
    }
}
