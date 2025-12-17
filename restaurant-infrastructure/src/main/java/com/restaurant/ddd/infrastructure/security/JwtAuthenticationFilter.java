package com.restaurant.ddd.infrastructure.security;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.User;
import com.restaurant.ddd.domain.service.UserDomainService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

/**
 * JWT Authentication Filter để xử lý JWT token từ request header.
 * Filter này sẽ:
 * 1. Extract JWT token từ Authorization header
 * 2. Validate token
 * 3. Extract thông tin user từ token
 * 4. Set authentication vào SecurityContext
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDomainService userDomainService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String token = extractTokenFromRequest(request);
        
        log.debug("JwtAuthenticationFilter - Request URI: {}, Token present: {}", requestURI, token != null);
        
        if (token != null) {
            try {
                log.debug("JwtAuthenticationFilter - Attempting to validate token...");
                // Validate token và extract claims
                io.jsonwebtoken.Claims claims = jwtService.decodeJwtToken(token, false);
                
                if (claims != null) {
                    log.debug("JwtAuthenticationFilter - Token validated successfully");
                    // Extract thông tin user từ claims
                    String username = claims.getSubject();
                    String userIdStr = claims.get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/primarysid", String.class);
                    String jitTokenStr = claims.get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/sid", String.class);
                    String email = claims.get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress", String.class);
                    String clientId = claims.get("clientId", String.class);
                    
                    log.debug("JwtAuthenticationFilter - Extracted claims - username: {}, userId: {}, email: {}, clientId: {}", 
                            username, userIdStr, email, clientId);
                    
                    if (userIdStr != null && username != null) {
                        UUID userId = UUID.fromString(userIdStr);
                        UUID jitToken = jitTokenStr != null ? UUID.fromString(jitTokenStr) : null;
                        
                        // Check user status - chỉ cho phép authenticate nếu status = ACTIVE
                        User user = userDomainService.findById(userId).orElse(null);
                        if (user == null) {
                            log.warn("JwtAuthenticationFilter - User not found: userId={}", userId);
                            filterChain.doFilter(request, response);
                            return;
                        }
                        
                        if (user.getStatus() == null || user.getStatus() != DataStatus.ACTIVE) {
                            log.warn("JwtAuthenticationFilter - User is not active: userId={}, status={}", userId, user.getStatus());
                            filterChain.doFilter(request, response);
                            return;
                        }
                        
                        // Tạo authentication object
                        // Sử dụng userId làm principal để có thể lấy được từ SecurityContext sau này
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userId.toString(), // principal
                                null, // credentials
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                        
                        // Set thông tin bổ sung vào authentication details
                        authentication.setDetails(new JwtAuthenticationDetails(userId, username, email, jitToken, clientId));
                        
                        // Set authentication vào SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.info("JWT authentication successful for user: {} (userId: {})", username, userId);
                    } else {
                        log.warn("JwtAuthenticationFilter - Missing required claims: username={}, userId={}", username, userIdStr);
                    }
                } else {
                    log.warn("JwtAuthenticationFilter - Token validation returned null claims");
                }
            } catch (Exception e) {
                log.error("JWT token validation failed for URI: {}", requestURI, e);
                // Không throw exception, để request tiếp tục (sẽ bị reject bởi SecurityConfig nếu cần auth)
            }
        } else {
            log.debug("JwtAuthenticationFilter - No token found in request for URI: {}", requestURI);
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token từ Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Class để lưu thông tin bổ sung trong authentication details
     */
    public static class JwtAuthenticationDetails {
        private final UUID userId;
        private final String username;
        private final String email;
        private final UUID jitToken;
        private final String clientId;

        public JwtAuthenticationDetails(UUID userId, String username, String email, UUID jitToken, String clientId) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.jitToken = jitToken;
            this.clientId = clientId;
        }

        public UUID getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public UUID getJitToken() {
            return jitToken;
        }

        public String getClientId() {
            return clientId;
        }
    }
}

