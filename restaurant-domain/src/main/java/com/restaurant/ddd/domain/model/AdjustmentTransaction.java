package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.AdjustmentType;
import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * AdjustmentTransaction - Phiếu điều chỉnh kho
 * Dùng cho kiểm kê, khấu hao, điều chỉnh tồn kho
 */
@Data
@Accessors(chain = true)
public class AdjustmentTransaction {
    private UUID id;
    private String transactionCode;
    private UUID warehouseId;
    private AdjustmentType adjustmentType;  // INCREASE or DECREASE
    private LocalDateTime transactionDate;
    private String reason;                   // Bắt buộc: Lý do điều chỉnh
    private String referenceNumber;
    private String notes;
    private Boolean isLocked;
    private DataStatus status;
    private UUID performedBy;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public void validate() {
        if (transactionCode == null || transactionCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu không được để trống");
        }
        if (warehouseId == null) {
            throw new IllegalArgumentException("Kho không được để trống");
        }
        if (adjustmentType == null) {
            throw new IllegalArgumentException("Loại điều chỉnh không được để trống");
        }
        if (transactionDate == null) {
            throw new IllegalArgumentException("Ngày điều chỉnh không được để trống");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Lý do điều chỉnh không được để trống");
        }
    }

    public void lock() {
        if (this.isLocked != null && this.isLocked) {
            throw new IllegalStateException("Phiếu điều chỉnh đã được chốt");
        }
        this.isLocked = true;
    }

    public void unlock() {
        if (this.isLocked == null || !this.isLocked) {
            throw new IllegalStateException("Phiếu điều chỉnh chưa được chốt");
        }
        this.isLocked = false;
    }
}
