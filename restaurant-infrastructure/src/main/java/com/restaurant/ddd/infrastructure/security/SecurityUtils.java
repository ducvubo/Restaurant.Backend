package com.restaurant.ddd.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * Utility class để lấy thông tin user từ SecurityContext
 */
public class SecurityUtils {

    /**
     * Lấy userId từ SecurityContext
     */
    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof String) {
                try {
                    return UUID.fromString((String) principal);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Lấy JwtAuthenticationDetails từ SecurityContext
     */
    public static JwtAuthenticationFilter.JwtAuthenticationDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object details = authentication.getDetails();
            if (details instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
                return (JwtAuthenticationFilter.JwtAuthenticationDetails) details;
            }
        }
        return null;
    }

    /**
     * Kiểm tra xem user đã đăng nhập chưa
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}

