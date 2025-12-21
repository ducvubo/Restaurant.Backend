package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Material - Nguyên vật liệu
 * Domain model for materials
 */
@Data
@Accessors(chain = true)
public class Material {
    private UUID id;
    private String code;
    private String name;
    private UUID categoryId;
    private String category; // Deprecated, kept for backward compatibility if needed, or effectively unused
    private BigDecimal unitPrice;
    private BigDecimal minStockLevel;
    private BigDecimal maxStockLevel;
    private String description;
    private DataStatus status;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    /**
     * Validate material data
     */
    public void validate() {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nguyên vật liệu không được để trống");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nguyên vật liệu không được để trống");
        }
        if (unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Đơn giá không được âm");
        }
    }

    /**
     * Check if stock is low
     */
    public boolean isLowStock(BigDecimal currentStock) {
        if (minStockLevel == null) return false;
        return currentStock.compareTo(minStockLevel) <= 0;
    }

    public void activate() {
        this.status = DataStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = DataStatus.INACTIVE;
    }
}
