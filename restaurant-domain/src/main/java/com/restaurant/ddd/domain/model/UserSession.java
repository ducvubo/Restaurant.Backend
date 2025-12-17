package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain Model for User Management Session
 * Represents a user's login session
 */
@Data
@Accessors(chain = true)
public class UserSession {
    private UUID id;
    private UUID userId;
    private UUID jitToken;
    private UUID jitRefreshToken;
    private String clientId;
    private LocalDateTime loginTime;
    private String loginIp;
    
    // Audit fields
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private DataStatus status;
}
