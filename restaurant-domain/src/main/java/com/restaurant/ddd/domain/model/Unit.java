package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Unit - Đơn vị tính
 * Domain model for measurement units
 */
@Data
@Accessors(chain = true)
public class Unit {
    private UUID id;
    private String code;
    private String name;
    private String symbol; // kg, L, pcs, box
    private UUID baseUnitId; // NULL if this is base unit
    private BigDecimal conversionRate; // Rate to convert to base unit
    private String description;
    private DataStatus status;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    /**
     * Check if this is a base unit
     */
    public boolean isBaseUnit() {
        return baseUnitId == null;
    }

    /**
     * Convert quantity to base unit
     */
    public BigDecimal convertToBaseUnit(BigDecimal quantity) {
        if (isBaseUnit()) {
            return quantity;
        }
        if (conversionRate == null) {
            throw new IllegalStateException("Conversion rate is required for derived units");
        }
        return quantity.multiply(conversionRate);
    }

    /**
     * Convert quantity from base unit to this unit
     */
    public BigDecimal convertFromBaseUnit(BigDecimal quantity) {
        if (isBaseUnit()) {
            return quantity;
        }
        if (conversionRate == null) {
            throw new IllegalStateException("Conversion rate is required for derived units");
        }
        return quantity.divide(conversionRate, 4, java.math.RoundingMode.HALF_UP);
    }

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
        if (!isBaseUnit() && conversionRate == null) {
            throw new IllegalArgumentException("Tỷ lệ chuyển đổi là bắt buộc cho đơn vị phái sinh");
        }
        if (conversionRate != null && conversionRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tỷ lệ chuyển đổi phải lớn hơn 0");
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
