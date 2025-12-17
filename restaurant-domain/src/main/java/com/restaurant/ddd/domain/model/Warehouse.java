package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.WarehouseType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Warehouse - Kho
 * Domain model for warehouse management
 */
@Data
@Accessors(chain = true)
public class Warehouse {
    private UUID id;
    private String code;
    private String name;
    private UUID branchId;
    private String address;
    private BigDecimal capacity;
    private UUID managerId;
    private WarehouseType warehouseType;
    private DataStatus status;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    /**
     * Check if this is a central warehouse
     */
    public boolean isCentral() {
        return WarehouseType.CENTRAL.equals(warehouseType);
    }

    /**
     * Validate warehouse data
     */
    public void validate() {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã kho không được để trống");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên kho không được để trống");
        }
        if (warehouseType == null) {
            throw new IllegalArgumentException("Loại kho không được để trống");
        }
        if (WarehouseType.BRANCH.equals(warehouseType) && branchId == null) {
            throw new IllegalArgumentException("Kho chi nhánh phải thuộc về một chi nhánh");
        }
    }

    public void activate() {
        this.status = DataStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = DataStatus.INACTIVE;
    }
}
