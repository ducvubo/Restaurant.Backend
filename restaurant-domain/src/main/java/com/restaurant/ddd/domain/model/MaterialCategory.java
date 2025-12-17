package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MaterialCategory - Danh mục nguyên vật liệu
 * Domain model for material categories
 */
@Data
@Accessors(chain = true)
public class MaterialCategory {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private DataStatus status;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public void validate() {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã danh mục không được để trống");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống");
        }
    }

    public void activate() {
        this.status = DataStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = DataStatus.INACTIVE;
    }
}
