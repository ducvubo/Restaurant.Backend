package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Branch Domain Model
 * Represents a restaurant branch
 */
@Data
@Accessors(chain = true)
public class Branch {
    private UUID id;
    private String code;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalTime openingTime;
    private LocalTime closingTime;
    
    // Audit fields
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private DataStatus status;
    
    // Business logic: Validate code
    public void validateCode() {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã chi nhánh không được để trống");
        }
    }
    
    // Business logic: Validate name
    public void validateName() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chi nhánh không được để trống");
        }
    }
    
    // Business logic: Validate time
    public void validateTime() {
        if (openingTime != null && closingTime != null) {
            if (openingTime.isAfter(closingTime) || openingTime.equals(closingTime)) {
                throw new IllegalArgumentException("Giờ mở cửa phải trước giờ đóng cửa");
            }
        }
    }
    
    // Business logic: Activate branch
    public void activate() {
        this.status = DataStatus.ACTIVE;
        this.updatedDate = LocalDateTime.now();
    }
    
    // Business logic: Deactivate branch
    public void deactivate() {
        this.status = DataStatus.INACTIVE;
        this.updatedDate = LocalDateTime.now();
    }
}
