package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.workflow.WorkflowNoteDTO;
import com.restaurant.ddd.application.model.workflow.WorkflowNoteRequest;

import java.util.List;
import java.util.UUID;

/**
 * Service interface cho Workflow Note CRUD
 */
public interface WorkflowNoteAppService {
    
    /**
     * Lấy danh sách notes theo referenceId và workflowType
     */
    List<WorkflowNoteDTO> getNotes(UUID referenceId, Integer workflowType);
    
    /**
     * Tạo note mới
     */
    WorkflowNoteDTO createNote(WorkflowNoteRequest request);
    
    /**
     * Cập nhật note (chỉ owner)
     */
    WorkflowNoteDTO updateNote(UUID noteId, WorkflowNoteRequest request);
    
    /**
     * Xóa note (chỉ owner)
     */
    void deleteNote(UUID noteId);
}
