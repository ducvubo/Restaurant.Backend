package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Unit - Đơn vị tính
 * Domain model for measurement units
 * Note: Unit conversions are managed separately in UnitConversion entity
 */
@Data
@Accessors(chain = true)
public class Unit {
    private UUID id;
    private String code;        // KG, L, THUNG (unique identifier)
    private String name;        // Kilogram, Lít, Thùng (display name)
    private String description;
    private DataStatus status;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    /**
     * Validate unit data
     */
    public void validate() {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã đơn vị không được để trống");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đơn vị không được để trống");
        }
    }

    /**
     * Activate unit
     */
    public void activate() {
        this.status = DataStatus.ACTIVE;
    }

    /**
     * Deactivate unit
     */
    public void deactivate() {
        this.status = DataStatus.INACTIVE;
    }
}
