package com.restaurant.ddd.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * AdjustmentItem - Chi tiết phiếu điều chỉnh kho
 */
@Data
@Accessors(chain = true)
public class AdjustmentItem {
    private UUID id;
    private UUID adjustmentTransactionId;
    private UUID materialId;
    private UUID unitId;
    private BigDecimal quantity;
    private UUID inventoryLedgerId;  // Lô hàng cụ thể (nullable - chỉ có khi từ kiểm kê)
    private String notes;
    private LocalDateTime createdDate;

    public void validate() {
        if (adjustmentTransactionId == null) {
            throw new IllegalArgumentException("Điều chỉnh không được để trống");
        }
        if (materialId == null) {
            throw new IllegalArgumentException("Nguyên liệu không được để trống");
        }
        if (unitId == null) {
            throw new IllegalArgumentException("Đơn vị tính không được để trống");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
    }
}
