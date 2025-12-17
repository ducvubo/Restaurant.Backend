package com.restaurant.ddd.controller.http.config;

import com.restaurant.ddd.application.service.PermissionService;
import com.restaurant.ddd.application.service.PolicyAppService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.UUID;

/**
 * Authorization Handler để kiểm tra policy động từ database dựa trên path.
 * Tương tự CoreAuthorizationMiddleware trong dự án C#.
 * 
 * Handler này sẽ:
 * 1. Lấy request path (ví dụ: /api/management/users/get)
 * 2. Lấy userId từ authentication context
 * 3. Lấy danh sách action keys của user từ database
 * 4. Dựa vào permission.json để lấy danh sách paths (patchRequire) mà user được phép
 * 5. Kiểm tra xem request path có trong danh sách paths được phép không
 * 6. Nếu không có quyền, throw AccessDeniedException
 */
@Component
@Slf4j
public class PolicyAuthorizationHandler implements HandlerInterceptor {

    @Autowired
    private PolicyAppService policyAppService;

    @Autowired
    private PermissionService permissionService;

    @Override
    public boolean preHandle(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        // Chỉ xử lý nếu là HandlerMethod (controller method)
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // Lấy request path (loại bỏ query string)
        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        // Bỏ qua các path không cần check quyền (như /api/management/auth/login, /api/management/permission/list)
        if (shouldSkipAuthorization(requestPath)) {
            return true;
        }

        // TODO: Lấy userId từ authentication context
        // Tạm thời return true vì permission check đã bị disable
        // Sau này khi bật lại, cần:
        // 1. Lấy userId từ SecurityContext
        // 2. Lấy danh sách action keys của user từ database
        // 3. Dựa vào permission.json để lấy danh sách paths được phép
        // 4. Kiểm tra request path có trong danh sách paths được phép không
        
        /*
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User chưa đăng nhập");
        }

        // Lấy userId từ authentication (cần implement trong JWT filter)
        UUID userId = getUserIdFromAuthentication(authentication);
        if (userId == null) {
            throw new AccessDeniedException("Không lấy được thông tin người dùng");
        }

        // Lấy danh sách action keys của user từ database
        List<String> userActionKeys = policyAppService.getUserPolicies(userId);
        
        // Dựa vào permission.json để lấy danh sách paths được phép
        boolean hasPermission = permissionService.hasPermissionForPath(userActionKeys, requestPath);
        
        if (!hasPermission) {
            log.warn("User {} không có quyền truy cập path: {}. Action keys: {}", 
                    userId, requestPath, userActionKeys);
            throw new AccessDeniedException("Bạn không có quyền truy cập tài nguyên này");
        }
        */

        return true;
    }

    /**
     * Kiểm tra xem có nên bỏ qua authorization cho path này không
     */
    private boolean shouldSkipAuthorization(String path) {
        // Bỏ qua các path public
        return path.startsWith("/api/management/auth/") ||
               path.startsWith("/api/management/permission/list") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/v3/api-docs");
    }

    /**
     * Lấy userId từ authentication context.
     * Cần implement trong JWT filter để set userId vào authentication.
     */
    private UUID getUserIdFromAuthentication(Authentication authentication) {
        // TODO: Implement logic để lấy userId từ authentication
        // Ví dụ: từ JWT claims hoặc từ principal
        return null;
    }
}

