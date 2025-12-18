package com.restaurant.ddd.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * InventoryCountItem - Chi tiết phiếu kiểm kê theo từng lô
 * Mỗi item = 1 lô cụ thể của 1 nguyên liệu
 */
@Data
@Accessors(chain = true)
public class InventoryCountItem {
    private UUID id;
    private UUID inventoryCountId;              // FK to InventoryCount
    private UUID materialId;                    // Nguyên liệu
    private UUID unitId;                        // Đơn vị tính
    private UUID inventoryLedgerId;             // Lô hàng cụ thể (FK to InventoryLedger)
    private String batchNumber;                 // Số lô (để hiển thị)
    private LocalDateTime transactionDate;      // Ngày nhập lô (để hiển thị)
    private BigDecimal systemQuantity;          // Số lượng theo sổ sách (remainingQuantity của batch)
    private BigDecimal actualQuantity;          // Số lượng thực tế đếm được
    private BigDecimal differenceQuantity;      // Chênh lệch (actualQuantity - systemQuantity)
    private String notes;                       // Ghi chú
    private LocalDateTime createdDate;

    public void validate() {
        if (inventoryCountId == null) {
            throw new IllegalArgumentException("Phiếu kiểm kê không được để trống");
        }
        if (materialId == null) {
            throw new IllegalArgumentException("Nguyên liệu không được để trống");
        }
        if (unitId == null) {
            throw new IllegalArgumentException("Đơn vị tính không được để trống");
        }
        if (inventoryLedgerId == null) {
            throw new IllegalArgumentException("Lô hàng không được để trống");
        }
        if (systemQuantity == null || systemQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số lượng sổ sách không hợp lệ");
        }
        if (actualQuantity == null || actualQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số lượng thực tế không hợp lệ");
        }
    }

    public void calculateDifference() {
        if (systemQuantity != null && actualQuantity != null) {
            this.differenceQuantity = actualQuantity.subtract(systemQuantity);
        }
    }

    public boolean hasDifference() {
        return differenceQuantity != null && differenceQuantity.compareTo(BigDecimal.ZERO) != 0;
    }

    public boolean isPositiveDifference() {
        return differenceQuantity != null && differenceQuantity.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegativeDifference() {
        return differenceQuantity != null && differenceQuantity.compareTo(BigDecimal.ZERO) < 0;
    }
}
