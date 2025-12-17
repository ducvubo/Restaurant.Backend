package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.user.LoginRequest;
import com.restaurant.ddd.application.model.user.LoginResponse;
import com.restaurant.ddd.application.model.user.RefreshTokenRequest;
import com.restaurant.ddd.application.model.user.UserDTO;
import com.restaurant.ddd.application.service.AuthAppService;
import com.restaurant.ddd.application.service.UserAppService;
import com.restaurant.ddd.domain.enums.ResultCode;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import com.restaurant.ddd.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

@RestController
@RequestMapping("/api/management/auth")
@Tag(name = "Authentication", description = "APIs for authentication and authorization")
@Slf4j
public class AuthController {

    @Autowired
    private AuthAppService authAppService;

    @Autowired
    private UserAppService userAppService;

    /**
     * Login
     * POST /api/management/auth/login
     */
    @Operation(summary = "User login", description = "Authenticate user and return access token and refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<ResultMessage<LoginResponse>> login(
            @RequestBody LoginRequest request,
            @RequestHeader(value = "CLIENT_ID", required = false) String clientId,
            HttpServletRequest httpRequest) {
        try {
            // Get IP address from request
            String ip = getClientIpAddress(httpRequest);
            
            LoginResponse response = authAppService.login(request, clientId, ip);
            return ResponseEntity.ok(ResultUtil.data(response, "Đăng nhập thành công"));
        } catch (RuntimeException e) {
            log.error("Login error: {}", e.getMessage());
            // Trả về 200 với success=false và message rõ ràng
            return ResponseEntity.ok(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), "Thông tin đăng nhập không chính xác"));
        }
    }

    /**
     * Logout
     * POST /api/management/auth/logout
     */
    @Operation(summary = "User logout", description = "Revoke refresh token and logout user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    @PostMapping("/logout")
    public ResponseEntity<ResultMessage<Void>> logout(@RequestBody RefreshTokenRequest request) {
        try {
            authAppService.logout(request.getRefreshToken());
            return ResponseEntity.ok(ResultUtil.data(null, "Đăng xuất thành công"));
        } catch (RuntimeException e) {
            log.error("Logout error: {}", e.getMessage());
            // Trả về 200 với success=false
            return ResponseEntity.ok(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), 
                e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Đăng xuất thất bại"));
        }
    }

    /**
     * Refresh token
     * POST /api/management/auth/refresh
     */
    @Operation(summary = "Refresh access token", description = "Get new access token and refresh token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<ResultMessage<LoginResponse>> refreshToken(
            @RequestBody RefreshTokenRequest request,
            @RequestHeader(value = "CLIENT_ID", required = false) String clientId,
            HttpServletRequest httpRequest) {
        try {
            // Get IP address
            String ip = getClientIpAddress(httpRequest);
            
            LoginResponse response = authAppService.refreshToken(request);
            return ResponseEntity.ok(ResultUtil.data(response, "Làm mới token thành công"));
        } catch (RuntimeException e) {
            log.error("Refresh token error: {}", e.getMessage());
            // Trả về 200 với success=false
            return ResponseEntity.ok(ResultUtil.error(ResultCode.USER_SESSION_EXPIRED.code(), 
                e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Làm mới token thất bại"));
        }
    }

    /**
     * Get current user information
     * GET /api/management/auth/me
     */
    @Operation(summary = "Get current user", description = "Lấy thông tin người dùng đang đăng nhập")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated")
    })
    @GetMapping("/me")
    public ResponseEntity<ResultMessage<UserDTO>> getCurrentUser() {
        try {
            // Lấy userId từ SecurityContext
            UUID userId = SecurityUtils.getCurrentUserId();
            
            if (userId == null) {
                log.warn("Get current user - No user ID found in SecurityContext");
                return ResponseEntity.ok(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), 
                    "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại."));
            }
            
            log.debug("Get current user - userId: {}", userId);
            
            // Lấy thông tin user từ database
            UserDTO user = userAppService.getUserById(userId);
            
            if (user == null) {
                log.warn("Get current user - User not found in database: {}", userId);
                return ResponseEntity.ok(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), 
                    "Không tìm thấy thông tin người dùng trong hệ thống."));
            }
            
            return ResponseEntity.ok(ResultUtil.data(user, "Lấy thông tin người dùng thành công"));
        } catch (RuntimeException e) {
            log.error("Get current user error: {}", e.getMessage(), e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), 
                e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "Lấy thông tin người dùng thất bại"));
        }
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip : "unknown";
    }
}

