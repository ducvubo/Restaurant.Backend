package com.restaurant.ddd.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception handler để xử lý lỗi authentication và trả về format chuẩn
 */
@Component
@Slf4j
public class SecurityExceptionHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.warn("Authentication failed for request: {} - {}", request.getRequestURI(), authException.getMessage());
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        // Tạo response theo format ResultMessage
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "Bạn cần đăng nhập để truy cập tài nguyên này");
        result.put("code", 401);
        result.put("timestamp", System.currentTimeMillis());
        result.put("result", null);
        
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

