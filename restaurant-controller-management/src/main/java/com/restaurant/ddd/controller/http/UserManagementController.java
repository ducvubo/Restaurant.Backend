package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.user.CreateUserRequest;
import com.restaurant.ddd.application.model.user.UpdateUserRequest;
import com.restaurant.ddd.application.model.user.UserDTO;
import com.restaurant.ddd.application.model.user.UserListRequest;
import com.restaurant.ddd.application.model.user.UserListResponse;
import com.restaurant.ddd.application.service.UserAppService;
import com.restaurant.ddd.domain.enums.ResultCode;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
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

@RestController
@RequestMapping("/api/management/users")
@Tag(name = "User Management (Admin)", description = "Admin APIs for managing users")
@Slf4j
public class UserManagementController {

    @Autowired
    private UserAppService userAppService;

    /**
     * Create new user
     * POST /api/management/users/add
     */
    @Operation(summary = "Create a new user", description = "Create a new user with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = ResultMessage.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    })
    @PostMapping("/add")
    public ResponseEntity<ResultMessage<UserDTO>> createUser(@RequestBody CreateUserRequest request) {
        try {
            UserDTO user = userAppService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResultUtil.data(user, "Tạo người dùng thành công"));
        } catch (RuntimeException e) {
            log.error("Error creating user: {}", e.getMessage());
            // Trả về 200 với success=false và message rõ ràng
            return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), 
                e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Tạo người dùng thất bại"));
        }
    }

    /**
     * Get user by ID
     * GET /api/management/users/get?id=...
     */
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = ResultMessage.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/get")
    public ResponseEntity<ResultMessage<UserDTO>> getUserById(
            @Parameter(description = "User ID", required = true) @RequestParam(name = "id") UUID id) {
        try {
            if (id == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), "ID người dùng không hợp lệ"));
            }
            UserDTO user = userAppService.getUserById(id);
            return ResponseEntity.ok(ResultUtil.data(user, ResultCode.SUCCESS));
        } catch (RuntimeException e) {
            log.error("Error getting user by ID: {}", e.getMessage());
            // Trả về 200 với success=false
            return ResponseEntity.ok(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), 
                e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Không tìm thấy người dùng"));
        }
    }

    /**
     * Get user by username
     * GET /api/management/users/getByUsername?username=...
     */
    @Operation(summary = "Get user by username", description = "Retrieve a user by their username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = ResultMessage.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/getByUsername")
    public ResponseEntity<ResultMessage<UserDTO>> getUserByUsername(
            @Parameter(description = "Username", required = true) @RequestParam(name = "username") String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), "Username không hợp lệ"));
            }
            UserDTO user = userAppService.getUserByUsername(username);
            return ResponseEntity.ok(ResultUtil.data(user, ResultCode.SUCCESS));
        } catch (RuntimeException e) {
            log.error("Error getting user by username: {}", e.getMessage());
            // Trả về 200 với success=false
            return ResponseEntity.ok(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), 
                e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Không tìm thấy người dùng"));
        }
    }

    /**
     * Get all users
     * GET /api/management/users/get-all
     */
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully",
            content = @Content(schema = @Schema(implementation = ResultMessage.class)))
    @GetMapping("/get-all")
    public ResultMessage<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userAppService.getAllUsers();
        return ResultUtil.data(users, ResultCode.SUCCESS);
    }

    /**
     * Update user
     * PUT /api/management/users/update
     */
    @Operation(summary = "Update user", description = "Update an existing user's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = ResultMessage.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/update")
    public ResponseEntity<ResultMessage<UserDTO>> updateUser(@RequestBody UpdateUserRequest request) {
        try {
            if (request.getId() == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), "ID người dùng không hợp lệ"));
            }
            UserDTO user = userAppService.updateUser(request.getId(), request);
            return ResponseEntity.ok(ResultUtil.data(user, "Cập nhật người dùng thành công"));
        } catch (RuntimeException e) {
            log.error("Error updating user: {}", e.getMessage());
            // Trả về 200 với success=false
            return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), 
                e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Cập nhật người dùng thất bại"));
        }
    }

    /**
     * Disable user (vô hiệu hóa người dùng)
     * PUT /api/management/users/disable?id=...
     */
    @Operation(summary = "Disable user", description = "Disable a user by setting isActive to false")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User disabled successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/disable")
    public ResponseEntity<ResultMessage<UserDTO>> disableUser(
            @Parameter(description = "User ID", required = true) @RequestParam(name = "id") UUID id) {
        try {
            if (id == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), "ID người dùng không hợp lệ"));
            }
            UserDTO user = userAppService.disableUser(id);
            return ResponseEntity.ok(ResultUtil.data(user, "Vô hiệu hóa người dùng thành công"));
        } catch (RuntimeException e) {
            log.error("Error disabling user: {}", e.getMessage());
            // Trả về 200 với success=false
            return ResponseEntity.ok(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), 
                e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Vô hiệu hóa người dùng thất bại"));
        }
    }

    /**
     * Enable user (kích hoạt lại người dùng)
     * PUT /api/management/users/enable?id=...
     */
    @Operation(summary = "Enable user", description = "Enable a user by setting isActive to true")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User enabled successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/enable")
    public ResponseEntity<ResultMessage<UserDTO>> enableUser(
            @Parameter(description = "User ID", required = true) @RequestParam(name = "id") UUID id) {
        try {
            if (id == null) {
                return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), "ID người dùng không hợp lệ"));
            }
            UserDTO user = userAppService.enableUser(id);
            return ResponseEntity.ok(ResultUtil.data(user, "Kích hoạt người dùng thành công"));
        } catch (RuntimeException e) {
            log.error("Error enabling user: {}", e.getMessage());
            // Trả về 200 với success=false
            return ResponseEntity.ok(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), 
                e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Kích hoạt người dùng thất bại"));
        }
    }

    /**
     * Lấy danh sách người dùng (phân trang, tìm kiếm)
     * GET /api/management/users/list
     */
    @Operation(summary = "Lấy danh sách người dùng", description = "Lấy danh sách người dùng với phân trang và tìm kiếm")
    @ApiResponse(responseCode = "200", description = "Lấy danh sách người dùng thành công",
            content = @Content(schema = @Schema(implementation = ResultMessage.class)))
    @GetMapping("/list")
    public ResultMessage<UserListResponse> getList(@ModelAttribute UserListRequest request) {
        UserListResponse response = userAppService.getList(request);
        return ResultUtil.data(response, "Lấy danh sách người dùng thành công");
    }
}

