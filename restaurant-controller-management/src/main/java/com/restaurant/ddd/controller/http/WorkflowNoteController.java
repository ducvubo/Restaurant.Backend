package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.workflow.WorkflowNoteDTO;
import com.restaurant.ddd.application.model.workflow.WorkflowNoteRequest;
import com.restaurant.ddd.application.service.WorkflowNoteAppService;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import com.restaurant.ddd.domain.enums.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/management/workflow-notes")
@Tag(name = "Workflow Notes", description = "APIs for managing workflow notes")
@Slf4j
public class WorkflowNoteController {

    @Autowired
    private WorkflowNoteAppService noteService;

    @Operation(summary = "Lấy danh sách ghi chú theo phiếu")
    @GetMapping("/list")
    public ResponseEntity<ResultMessage<List<WorkflowNoteDTO>>> getNotes(
            @RequestParam("referenceId") UUID referenceId,
            @RequestParam("workflowType") Integer workflowType) {
        try {
            List<WorkflowNoteDTO> result = noteService.getNotes(referenceId, workflowType);
            return ResponseEntity.ok(ResultUtil.data(result, "Lấy danh sách ghi chú thành công"));
        } catch (Exception e) {
            log.error("Error getting notes", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Tạo ghi chú mới")
    @PostMapping("/create")
    public ResponseEntity<ResultMessage<WorkflowNoteDTO>> createNote(@RequestBody WorkflowNoteRequest request) {
        try {
            WorkflowNoteDTO result = noteService.createNote(request);
            return ResponseEntity.ok(ResultUtil.data(result, "Tạo ghi chú thành công"));
        } catch (Exception e) {
            log.error("Error creating note", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Cập nhật ghi chú (chỉ owner)")
    @PutMapping("/update")
    public ResponseEntity<ResultMessage<WorkflowNoteDTO>> updateNote(
            @RequestParam("id") UUID id,
            @RequestBody WorkflowNoteRequest request) {
        try {
            WorkflowNoteDTO result = noteService.updateNote(id, request);
            return ResponseEntity.ok(ResultUtil.data(result, "Cập nhật ghi chú thành công"));
        } catch (Exception e) {
            log.error("Error updating note", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Xóa ghi chú (chỉ owner)")
    @DeleteMapping("/delete")
    public ResponseEntity<ResultMessage<Void>> deleteNote(@RequestParam("id") UUID id) {
        try {
            noteService.deleteNote(id);
            return ResponseEntity.ok(ResultUtil.data(null, "Xóa ghi chú thành công"));
        } catch (Exception e) {
            log.error("Error deleting note", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }
}
