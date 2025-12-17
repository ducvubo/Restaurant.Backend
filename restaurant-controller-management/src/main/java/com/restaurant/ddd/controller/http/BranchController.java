package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.branch.BranchDTO;
import com.restaurant.ddd.application.model.branch.BranchListRequest;
import com.restaurant.ddd.application.model.branch.BranchListResponse;
import com.restaurant.ddd.application.model.branch.CreateBranchRequest;
import com.restaurant.ddd.application.model.branch.UpdateBranchRequest;
import com.restaurant.ddd.application.service.BranchAppService;
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
 * API quản lý chi nhánh (Branch) cho trang Management
 */
@RestController
@RequestMapping("/api/management/branches")
@Tag(name = "Branch Management (Admin)", description = "Admin APIs for managing branches")
@Slf4j
public class BranchController {

    @Autowired
    private BranchAppService branchAppService;

    /**
     * Thêm mới chi nhánh
     * POST /api/management/branches/create
     */
    @Operation(summary = "Thêm mới chi nhánh", description = "Tạo một chi nhánh mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Chi nhánh được tạo thành công",
                    content = @Content(schema = @Schema(implementation = ResultMessage.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ")
    })
    @PostMapping("/create")
    public ResponseEntity<ResultMessage<BranchDTO>> create(@RequestBody CreateBranchRequest request) {
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại."));
            }
            BranchDTO branch = branchAppService.create(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResultUtil.data(branch, "Thêm mới chi nhánh thành công"));
        } catch (RuntimeException e) {
            log.error("Error creating branch: {}", e.getMessage());
            return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(),
                    e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Thêm mới chi nhánh thất bại"));
        }
    }

    /**
     * Cập nhật chi nhánh
     * PUT /api/management/branches/update?id=...
     */
    @Operation(summary = "Cập nhật chi nhánh", description = "Cập nhật thông tin của một chi nhánh")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chi nhánh được cập nhật thành công",
                    content = @Content(schema = @Schema(implementation = ResultMessage.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi nhánh")
    })
    @PutMapping("/update")
    public ResponseEntity<ResultMessage<BranchDTO>> update(
            @Parameter(description = "ID chi nhánh", required = true) @RequestParam(name = "id") UUID id,
            @RequestBody UpdateBranchRequest request) {
        try {
            if (id == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), "ID chi nhánh không hợp lệ"));
            }
            UUID userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại."));
            }
            request.setId(id);
            BranchDTO branch = branchAppService.update(request, userId);
            return ResponseEntity.ok(ResultUtil.data(branch, "Cập nhật chi nhánh thành công"));
        } catch (RuntimeException e) {
            log.error("Error updating branch: {}", e.getMessage());
            return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(),
                    e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Cập nhật chi nhánh thất bại"));
        }
    }

    /**
     * Lấy thông tin chi nhánh theo ID
     * GET /api/management/branches/get?id=...
     */
    @Operation(summary = "Lấy thông tin chi nhánh theo ID", description = "Lấy thông tin chi tiết của một chi nhánh")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy chi nhánh",
                    content = @Content(schema = @Schema(implementation = ResultMessage.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi nhánh")
    })
    @GetMapping("/get")
    public ResponseEntity<ResultMessage<BranchDTO>> getById(
            @Parameter(description = "ID chi nhánh", required = true) @RequestParam(name = "id") UUID id) {
        try {
            if (id == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), "ID chi nhánh không hợp lệ"));
            }
            BranchDTO branch = branchAppService.getById(id);
            return ResponseEntity.ok(ResultUtil.data(branch, "Lấy thông tin chi nhánh thành công"));
        } catch (RuntimeException e) {
            log.error("Error getting branch by ID: {}", e.getMessage());
            return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(),
                    e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Không tìm thấy chi nhánh"));
        }
    }

    /**
     * Lấy tất cả chi nhánh
     * GET /api/management/branches/get-all
     */
    @Operation(summary = "Lấy tất cả chi nhánh", description = "Lấy danh sách tất cả chi nhánh")
    @ApiResponse(responseCode = "200", description = "Lấy danh sách chi nhánh thành công",
            content = @Content(schema = @Schema(implementation = ResultMessage.class)))
    @GetMapping("/get-all")
    public ResultMessage<List<BranchDTO>> getAll() {
        List<BranchDTO> branches = branchAppService.getAll();
        return ResultUtil.data(branches, "Lấy danh sách chi nhánh thành công");
    }

    /**
     * Lấy tất cả chi nhánh đang hoạt động
     * GET /api/management/branches/get-all-active
     */
    @Operation(summary = "Lấy tất cả chi nhánh đang hoạt động", description = "Lấy danh sách chi nhánh có status = ACTIVE")
    @ApiResponse(responseCode = "200", description = "Lấy danh sách chi nhánh thành công",
            content = @Content(schema = @Schema(implementation = ResultMessage.class)))
    @GetMapping("/get-all-active")
    public ResultMessage<List<BranchDTO>> getAllActive() {
        List<BranchDTO> branches = branchAppService.getAllActive();
        return ResultUtil.data(branches, "Lấy danh sách chi nhánh hoạt động thành công");
    }

    /**
     * Kích hoạt chi nhánh
     * PUT /api/management/branches/activate?id=...
     */
    @Operation(summary = "Kích hoạt chi nhánh", description = "Kích hoạt một chi nhánh (set status = ACTIVE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chi nhánh được kích hoạt thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi nhánh")
    })
    @PutMapping("/activate")
    public ResponseEntity<ResultMessage<BranchDTO>> activate(
            @Parameter(description = "ID chi nhánh", required = true) @RequestParam(name = "id") UUID id) {
        try {
            if (id == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), "ID chi nhánh không hợp lệ"));
            }
            UUID userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại."));
            }
            BranchDTO branch = branchAppService.activate(id, userId);
            return ResponseEntity.ok(ResultUtil.data(branch, "Kích hoạt chi nhánh thành công"));
        } catch (RuntimeException e) {
            log.error("Error activating branch: {}", e.getMessage());
            return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(),
                    e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Kích hoạt chi nhánh thất bại"));
        }
    }

    /**
     * Vô hiệu hóa chi nhánh
     * PUT /api/management/branches/deactivate?id=...
     */
    @Operation(summary = "Vô hiệu hóa chi nhánh", description = "Vô hiệu hóa một chi nhánh (set status = INACTIVE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chi nhánh được vô hiệu hóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi nhánh")
    })
    @PutMapping("/deactivate")
    public ResponseEntity<ResultMessage<BranchDTO>> deactivate(
            @Parameter(description = "ID chi nhánh", required = true) @RequestParam(name = "id") UUID id) {
        try {
            if (id == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), "ID chi nhánh không hợp lệ"));
            }
            UUID userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại."));
            }
            BranchDTO branch = branchAppService.deactivate(id, userId);
            return ResponseEntity.ok(ResultUtil.data(branch, "Vô hiệu hóa chi nhánh thành công"));
        } catch (RuntimeException e) {
            log.error("Error deactivating branch: {}", e.getMessage());
            return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(),
                    e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Vô hiệu hóa chi nhánh thất bại"));
        }
    }

    /**
     * Lấy danh sách chi nhánh (phân trang, tìm kiếm)
     * GET /api/management/branches/list
     */
    @Operation(summary = "Lấy danh sách chi nhánh", description = "Lấy danh sách chi nhánh với phân trang và tìm kiếm")
    @ApiResponse(responseCode = "200", description = "Lấy danh sách chi nhánh thành công",
            content = @Content(schema = @Schema(implementation = ResultMessage.class)))
    @GetMapping("/list")
    public ResultMessage<BranchListResponse> getList(@ModelAttribute BranchListRequest request) {
        BranchListResponse response = branchAppService.getList(request);
        return ResultUtil.data(response, "Lấy danh sách chi nhánh thành công");
    }
}
