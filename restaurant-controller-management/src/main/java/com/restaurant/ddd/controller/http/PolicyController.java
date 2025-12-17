package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.user.CreatePolicyRequest;
import com.restaurant.ddd.application.model.user.PolicyDTO;
import com.restaurant.ddd.application.model.user.PolicyListRequest;
import com.restaurant.ddd.application.model.user.PolicyListResponse;
import com.restaurant.ddd.application.model.user.UpdatePolicyRequest;
import com.restaurant.ddd.application.service.PolicyAppService;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import com.restaurant.ddd.domain.enums.ResultCode;
import com.restaurant.ddd.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * API quản lý tập quyền (Policy) cho trang Management
 */
@RestController
@RequestMapping("/api/management/policy")
@Tag(name = "Policy Management (Admin)", description = "Admin APIs for managing policies")
@Slf4j
public class PolicyController {

    @Autowired
    private PolicyAppService policyAppService;

    /**
     * Thêm mới tập quyền
     */
    @Operation(summary = "Thêm mới tập quyền", description = "Tạo một tập quyền mới với danh sách permission keys")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tập quyền được tạo thành công",
                    content = @Content(schema = @Schema(implementation = ResultMessage.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ")
    })
    @PostMapping("/add")
    public ResponseEntity<ResultMessage<PolicyDTO>> add(@RequestBody CreatePolicyRequest request) {
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại."));
            }
            PolicyDTO policy = policyAppService.create(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResultUtil.data(policy, "Thêm mới tập quyền thành công"));
        } catch (RuntimeException e) {
            log.error("Error creating policy: {}", e.getMessage());
            return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(),
                    e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Thêm mới tập quyền thất bại"));
        }
    }

    /**
     * Cập nhật tập quyền
     */
    @Operation(summary = "Cập nhật tập quyền", description = "Cập nhật thông tin của một tập quyền")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tập quyền được cập nhật thành công",
                    content = @Content(schema = @Schema(implementation = ResultMessage.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tập quyền")
    })
    @PutMapping("/update")
    public ResponseEntity<ResultMessage<PolicyDTO>> update(@RequestBody UpdatePolicyRequest request) {
        try {
            if (request.getId() == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), "ID tập quyền không hợp lệ"));
            }
            UUID userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại."));
            }
            PolicyDTO policy = policyAppService.update(request, userId);
            return ResponseEntity.ok(ResultUtil.data(policy, "Cập nhật tập quyền thành công"));
        } catch (RuntimeException e) {
            log.error("Error updating policy: {}", e.getMessage());
            return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(),
                    e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Cập nhật tập quyền thất bại"));
        }
    }

    /**
     * Lấy thông tin tập quyền theo Id
     */
    @Operation(summary = "Lấy thông tin tập quyền theo ID", description = "Lấy thông tin chi tiết của một tập quyền")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy tập quyền",
                    content = @Content(schema = @Schema(implementation = ResultMessage.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tập quyền")
    })
    @GetMapping("/get")
    public ResponseEntity<ResultMessage<PolicyDTO>> getById(
            @Parameter(description = "ID tập quyền", required = true) @RequestParam(name = "id") UUID id) {
        try {
            if (id == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), "ID tập quyền không hợp lệ"));
            }
            PolicyDTO policy = policyAppService.getById(id);
            return ResponseEntity.ok(ResultUtil.data(policy, "Lấy thông tin tập quyền thành công"));
        } catch (RuntimeException e) {
            log.error("Error getting policy by ID: {}", e.getMessage());
            return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(),
                    e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Không tìm thấy tập quyền"));
        }
    }

    /**
     * Xóa tập quyền
     */
    @Operation(summary = "Xóa tập quyền", description = "Xóa một tập quyền theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tập quyền được xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tập quyền")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<ResultMessage<Void>> delete(
            @Parameter(description = "ID tập quyền", required = true) @RequestParam(name = "id") UUID id) {
        try {
            if (id == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), "ID tập quyền không hợp lệ"));
            }
            policyAppService.delete(id);
            return ResponseEntity.ok(ResultUtil.data(null, "Xóa tập quyền thành công"));
        } catch (RuntimeException e) {
            log.error("Error deleting policy: {}", e.getMessage());
            return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(),
                    e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Xóa tập quyền thất bại"));
        }
    }

    /**
     * Lấy danh sách tập quyền (phân trang, tìm kiếm)
     */
    @Operation(summary = "Lấy danh sách tập quyền", description = "Lấy danh sách tập quyền với phân trang và tìm kiếm")
    @ApiResponse(responseCode = "200", description = "Lấy danh sách tập quyền thành công",
            content = @Content(schema = @Schema(implementation = ResultMessage.class)))
    @GetMapping("/list")
    public ResultMessage<PolicyListResponse> getList(@ModelAttribute PolicyListRequest request) {
        PolicyListResponse response = policyAppService.getList(request);
        return ResultUtil.data(response, "Lấy danh sách tập quyền thành công");
    }

    /**
     * Lấy tất cả tập quyền đang Active (dùng cho Workflow, v.v...)
     */
    @Operation(summary = "Lấy tất cả tập quyền đang Active", description = "Lấy danh sách tất cả tập quyền đang hoạt động")
    @ApiResponse(responseCode = "200", description = "Lấy danh sách tập quyền thành công",
            content = @Content(schema = @Schema(implementation = ResultMessage.class)))
    @GetMapping("/all")
    public ResultMessage<List<PolicyDTO>> getAll() {
        List<PolicyDTO> policies = policyAppService.getAll();
        return ResultUtil.data(policies, "Lấy danh sách tập quyền thành công");
    }
}

