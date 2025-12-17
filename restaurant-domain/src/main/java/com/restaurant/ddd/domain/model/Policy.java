package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Policy Domain Model (Rich Model).
 * Contains business logic and validation.
 * Decoupled from Infrastructure/JPA.
 */
@Getter
@NoArgsConstructor
public class Policy {

    private UUID id;
    private String name;
    private String description;
    private List<String> policies; // Changed from String JSON to List<String> for better domain usage
    
    // Base Audit fields
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private DataStatus status;

    public Policy(String name, String description, List<String> policies) {
        this.id = UUID.randomUUID();
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
        this.status = DataStatus.ACTIVE;
        
        setName(name);
        this.description = description;
        this.policies = policies != null ? policies : new ArrayList<>();
    }

    // Business Logic: Validate Name
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Policy name cannot be empty");
        }
        this.name = name;
    }

    // Business Logic: Update Details
    public void updateDetails(String name, String description, List<String> policies) {
        setName(name);
        this.description = description;
        if (policies != null) {
            this.policies = policies;
        }
        this.updatedDate = LocalDateTime.now();
    }
    
    // Business Logic: Activate/Deactivate
    public void activate() {
        this.status = DataStatus.ACTIVE;
        this.updatedDate = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = DataStatus.INACTIVE;
        this.updatedDate = LocalDateTime.now();
    }

    // Setters for Infrastructure/Reconstitution ONLY (Package-private or careful usage)
    // For simplicity in this demo, we can allow public setters or a builder for the Mapper to use.
    public void setId(UUID id) { this.id = id; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(UUID updatedBy) { this.updatedBy = updatedBy; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
    public void setStatus(DataStatus status) { this.status = status; }
    public void setPolicies(List<String> policies) { this.policies = policies; }
    public void setDescription(String description) { this.description = description; }
}
