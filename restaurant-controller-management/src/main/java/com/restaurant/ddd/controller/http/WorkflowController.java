package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.workflow.*;
import com.restaurant.ddd.application.service.WorkflowAppService;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import com.restaurant.ddd.domain.enums.ResultCode;
import com.restaurant.ddd.domain.enums.WorkflowType;
import com.restaurant.ddd.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * REST API Controller cho Workflow Management
 */
@Slf4j
@RestController
@RequestMapping("/api/management/workflows")
@RequiredArgsConstructor
@Tag(name = "Workflow Management", description = "API quản lý quy trình làm việc")
@CrossOrigin
public class WorkflowController {

    private final WorkflowAppService workflowAppService;

    @PostMapping("/create")
    @Operation(summary = "Tạo quy trình mới", description = "Tạo workflow mới với BPMN diagram")
    public ResponseEntity<ResultMessage<WorkflowDTO>> create(
            @RequestBody CreateWorkflowRequest request,
            @RequestParam(name = "isForceActive", defaultValue = "false") boolean isForceActive) {
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            var result = workflowAppService.create(request, userId, isForceActive);
            if (result.getCode() == ResultCode.SUCCESS) {
                return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
            }
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
        } catch (Exception e) {
            log.error("Error creating workflow: {}", e.getMessage(), e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), "Tạo quy trình thất bại: " + e.getMessage()));
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Cập nhật quy trình", description = "Cập nhật thông tin quy trình")
    public ResponseEntity<ResultMessage<WorkflowDTO>> update(
            @RequestParam(name = "id") UUID id,
            @RequestBody UpdateWorkflowRequest request,
            @RequestParam(name = "isForceActive", defaultValue = "false") boolean isForceActive) {
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            var result = workflowAppService.update(id, request, userId, isForceActive);
            if (result.getCode() == ResultCode.SUCCESS) {
                return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
            }
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
        } catch (Exception e) {
            log.error("Error updating workflow: {}", e.getMessage(), e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), "Cập nhật quy trình thất bại: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Xóa quy trình", description = "Xóa quy trình (chỉ có thể xóa nếu không ACTIVE)")
    public ResponseEntity<ResultMessage<String>> delete(@RequestParam(name = "id") UUID id) {
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            var result = workflowAppService.delete(id, userId);
            if (result.getCode() == ResultCode.SUCCESS) {
                return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
            }
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting workflow: {}", e.getMessage(), e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), "Xóa quy trình thất bại: " + e.getMessage()));
        }
    }

    @GetMapping("/get")
    @Operation(summary = "Lấy chi tiết quy trình", description = "Lấy thông tin chi tiết quy trình theo ID")
    public ResponseEntity<ResultMessage<WorkflowDTO>> getById(@RequestParam(name = "id") UUID id) {
        try {
            var result = workflowAppService.getById(id);
            if (result.getCode() == ResultCode.SUCCESS) {
                return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
            }
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
        } catch (Exception e) {
            log.error("Error getting workflow: {}", e.getMessage(), e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), "Lấy quy trình thất bại: " + e.getMessage()));
        }
    }

    @GetMapping("/list")
    @Operation(summary = "Lấy danh sách quy trình", description = "Lấy danh sách quy trình với phân trang")
    public ResponseEntity<ResultMessage<WorkflowListResponse>> getList(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortDirection", required = false) String sortDirection,
            @RequestParam(name = "workflowType", required = false) Integer workflowType,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "keyword", required = false) String keyword) {
        try {
            WorkflowListRequest request = new WorkflowListRequest();
            if (page != null) request.setPage(page);
            if (size != null) request.setSize(size);
            request.setSortBy(sortBy);
            request.setSortDirection(sortDirection);
            request.setWorkflowType(workflowType);
            request.setStatus(status);
            request.setKeyword(keyword);
            
            var result = workflowAppService.getList(request);
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        } catch (Exception e) {
            log.error("Error getting workflows: {}", e.getMessage(), e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), "Lấy danh sách quy trình thất bại: " + e.getMessage()));
        }
    }

    @GetMapping("/get-active-by-type")
    @Operation(summary = "Lấy quy trình đang kích hoạt", description = "Lấy quy trình đang kích hoạt theo loại")
    public ResponseEntity<ResultMessage<WorkflowDTO>> getActiveByType(
            @RequestParam(name = "workflowType") WorkflowType workflowType) {
        try {
            var result = workflowAppService.getActiveByType(workflowType);
            if (result.getCode() == ResultCode.SUCCESS) {
                return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
            }
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
        } catch (Exception e) {
            log.error("Error getting active workflow: {}", e.getMessage(), e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), "Lấy quy trình thất bại: " + e.getMessage()));
        }
    }

    @PutMapping("/activate")
    @Operation(summary = "Kích hoạt quy trình", description = "Thay đổi trạng thái quy trình thành ACTIVE")
    public ResponseEntity<ResultMessage<String>> activate(
            @RequestParam(name = "id") UUID id,
            @RequestParam(name = "isForceActive", defaultValue = "false") boolean isForceActive) {
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            var result = workflowAppService.activate(id, userId, isForceActive);
            if (result.getCode() == ResultCode.SUCCESS) {
                return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
            }
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
        } catch (Exception e) {
            log.error("Error activating workflow: {}", e.getMessage(), e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), "Kích hoạt quy trình thất bại: " + e.getMessage()));
        }
    }

    @PutMapping("/deactivate")
    @Operation(summary = "Vô hiệu hóa quy trình", description = "Thay đổi trạng thái quy trình thành INACTIVE")
    public ResponseEntity<ResultMessage<String>> deactivate(@RequestParam(name = "id") UUID id) {
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            var result = workflowAppService.deactivate(id, userId);
            if (result.getCode() == ResultCode.SUCCESS) {
                return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
            }
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
        } catch (Exception e) {
            log.error("Error deactivating workflow: {}", e.getMessage(), e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), "Vô hiệu hóa quy trình thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/validate-bpmn")
    @Operation(summary = "Kiểm tra BPMN XML", description = "Kiểm tra tính hợp lệ của BPMN diagram")
    public ResponseEntity<BpmnValidationResult> validateBpmn(@RequestBody Map<String, String> request) {
        try {
            String bpmnXml = request.get("bpmnXml");
            BpmnValidationResult result = workflowAppService.validateBpmn(bpmnXml);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error validating BPMN: {}", e.getMessage(), e);
            BpmnValidationResult errorResult = new BpmnValidationResult(false);
            errorResult.addError("Lỗi kiểm tra BPMN: " + e.getMessage());
            return ResponseEntity.ok(errorResult);
        }
    }

    @PostMapping("/extract-policy-ids")
    @Operation(summary = "Trích xuất Policy IDs", description = "Trích xuất danh sách Policy IDs từ BPMN diagram")
    public ResponseEntity<ResultMessage<?>> extractPolicyIds(@RequestBody Map<String, String> request) {
        try {
            String bpmnXml = request.get("bpmnXml");
            java.util.List<String> policyIds = workflowAppService.extractPolicyIdsFromBpmn(bpmnXml);
            return ResponseEntity.ok(ResultUtil.data(policyIds, "Trích xuất thành công"));
        } catch (Exception e) {
            log.error("Error extracting policy IDs: {}", e.getMessage(), e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), "Trích xuất thất bại: " + e.getMessage()));
        }
    }
}
