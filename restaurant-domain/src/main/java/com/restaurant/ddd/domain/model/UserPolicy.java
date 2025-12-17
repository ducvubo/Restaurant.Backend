package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain Model for User-Policy mapping
 * Represents the relationship between a User and their assigned Policies
 */
@Data
@Accessors(chain = true)
public class UserPolicy {
    private UUID id;
    private UUID userId;
    private UUID policyId;
    
    // Audit fields
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private DataStatus status;
}
