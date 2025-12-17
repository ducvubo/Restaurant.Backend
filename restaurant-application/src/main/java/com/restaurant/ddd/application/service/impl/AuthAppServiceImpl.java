package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.UserMapper;
import com.restaurant.ddd.application.model.user.LoginRequest;
import com.restaurant.ddd.application.model.user.LoginResponse;
import com.restaurant.ddd.application.model.user.RefreshTokenRequest;
import com.restaurant.ddd.application.model.user.UserDTO;
import com.restaurant.ddd.application.service.AuthAppService;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.User;
import com.restaurant.ddd.domain.model.UserSession;
import com.restaurant.ddd.domain.respository.AccountSessionRepository;
import com.restaurant.ddd.domain.service.AuthDomainService;
import com.restaurant.ddd.infrastructure.security.JwtService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AuthAppServiceImpl implements AuthAppService {

    @Autowired
    private AuthDomainService authDomainService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountSessionRepository accountSessionRepository;

    @Override
    public LoginResponse login(LoginRequest request, String clientId, String ip) {
        log.info("Application Service: login - {}", request.getUsername());

        // Validate clientId
        if (clientId == null || clientId.isEmpty()) {
            throw new RuntimeException("CLIENT_ID is required in header");
        }

        // Find user by username
        User user = authDomainService.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Check user status - chỉ cho phép login nếu status = ACTIVE
        if (user.getStatus() == null || user.getStatus() != DataStatus.ACTIVE) {
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa hoặc không hoạt động");
        }

        // Validate password
        if (!authDomainService.validatePassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Get or create session
        UserSession session = accountSessionRepository
                .findByClientIdAndAccId(clientId, user.getId())
                .orElse(null);

        if (session == null || session.getId() == null) {
            session = new UserSession();
            session.setId(UUID.randomUUID());
        }

        // Generate JIT tokens
        UUID jitToken = UUID.randomUUID();
        UUID jitRefreshToken = UUID.randomUUID();

        // Build tokens với JIT
        String accessToken = jwtService.buildToken(
                clientId,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                jitToken
        );

        String refreshToken = jwtService.generateRefreshToken(
                clientId,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                jitRefreshToken
        );

        // Update session
        session.setUserId(user.getId());
        session.setClientId(clientId);
        session.setLoginIp(ip != null ? ip : "unknown");
        session.setLoginTime(LocalDateTime.now());
        session.setJitToken(jitToken);
        session.setJitRefreshToken(jitRefreshToken);

        accountSessionRepository.save(session);

        // Convert user to DTO
        UserDTO userDTO = UserMapper.toDTO(user);

        // Build response
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(86400L); // 24 hours in seconds
        response.setUser(userDTO);

        return response;
    }

    @Override
    public void logout(String refreshToken) {
        log.info("Application Service: logout");
        
        // Decode refresh token để lấy JIT
        Claims decodedToken = jwtService.decodeJwtToken(refreshToken, true);
        if (decodedToken == null) {
            return;
        }

        UUID jitRefreshToken = jwtService.extractJitRefreshToken(refreshToken);
        UUID userId = jwtService.extractUserIdFromClaims(refreshToken, true);
        String clientId = decodedToken.get("clientId", String.class);

        if (jitRefreshToken != null && userId != null && clientId != null) {
            Optional<UUID> sessionId = accountSessionRepository
                    .findByJitRefreshTokenAndClientIdAndAccId(jitRefreshToken, clientId, userId);
            
            if (sessionId.isPresent()) {
                accountSessionRepository.deleteById(sessionId.get());
            }
        }
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        log.info("Application Service: refreshToken");

        // Decode refresh token
        Claims decodedToken = jwtService.decodeJwtToken(request.getRefreshToken(), true);
        if (decodedToken == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Extract information from token
        UUID userId = jwtService.extractUserIdFromClaims(request.getRefreshToken(), true);
        UUID jitRefreshToken = jwtService.extractJitRefreshToken(request.getRefreshToken());
        String clientId = decodedToken.get("clientId", String.class);

        if (userId == null || jitRefreshToken == null || clientId == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Find session by JIT refresh token
        Optional<UUID> sessionId = accountSessionRepository
                .findByJitRefreshTokenAndClientIdAndAccId(jitRefreshToken, clientId, userId);

        if (sessionId.isEmpty() || sessionId.get().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
            throw new RuntimeException("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        // Get session
        UserSession session = accountSessionRepository.findById(sessionId.get())
                .orElseThrow(() -> new RuntimeException("Phiên làm việc không tồn tại"));

        // Get user
        User user = authDomainService.findByUsername(decodedToken.getSubject())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        // Check user status - chỉ cho phép refresh nếu status = ACTIVE
        if (user.getStatus() == null || user.getStatus() != DataStatus.ACTIVE) {
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa hoặc không hoạt động");
        }

        // Generate new JIT tokens
        UUID jitTokenNew = UUID.randomUUID();
        UUID jitRefreshTokenNew = UUID.randomUUID();

        // Build new tokens
        String newAccessToken = jwtService.buildToken(
                clientId,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                jitTokenNew
        );

        String newRefreshToken = jwtService.generateRefreshToken(
                clientId,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                jitRefreshTokenNew
        );

        // Update session
        session.setUserId(user.getId());
        session.setClientId(clientId);
        session.setLoginTime(LocalDateTime.now());
        session.setJitToken(jitTokenNew);
        session.setJitRefreshToken(jitRefreshTokenNew);

        accountSessionRepository.save(session);

        // Convert user to DTO
        UserDTO userDTO = UserMapper.toDTO(user);

        // Build response
        LoginResponse response = new LoginResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setExpiresIn(86400L); // 24 hours in seconds
        response.setUser(userDTO);

        return response;
    }
}
