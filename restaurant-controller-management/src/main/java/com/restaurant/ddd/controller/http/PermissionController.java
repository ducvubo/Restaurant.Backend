package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.user.PermissionModel;
import com.restaurant.ddd.application.service.PermissionService;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API để quản lý permissions
 */
@RestController
@RequestMapping("/api/management/permission")
@Tag(name = "Permission Management", description = "APIs for managing permissions")
@Slf4j
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * Lấy danh sách permissions từ file permission.json
     * GET /api/management/permission/list
     */
    @Operation(summary = "Lấy danh sách permissions", description = "Lấy toàn bộ cấu trúc permissions từ file permission.json")
    @ApiResponse(responseCode = "200", description = "Lấy danh sách permissions thành công")
    @GetMapping("/list")
    public ResponseEntity<ResultMessage<List<PermissionModel>>> getPermissions() {
        List<PermissionModel> permissions = permissionService.getPermissions();
        return ResponseEntity.ok(ResultUtil.data(permissions, "Lấy danh sách permissions thành công"));
    }

    /**
     * Reload permissions từ file
     * POST /api/management/permission/reload
     */
    @Operation(summary = "Reload permissions", description = "Tải lại permissions từ file permission.json")
    @ApiResponse(responseCode = "200", description = "Reload permissions thành công")
    @PostMapping("/reload")
    public ResponseEntity<ResultMessage<Void>> reloadPermissions() {
        permissionService.reload();
        return ResponseEntity.ok(ResultUtil.data(null, "Reload permissions thành công"));
    }
}

