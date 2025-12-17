package com.restaurant.ddd.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * StockInItem - Chi tiết phiếu nhập kho
 * Đại diện cho một dòng nguyên liệu trong phiếu nhập
 */
@Data
@Accessors(chain = true)
public class StockInItem {
    private UUID id;
    private UUID stockInTransactionId;  // FK to STOCK_IN_TRANSACTIONS
    private UUID materialId;
    private UUID unitId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;   // = quantity * unitPrice
    private String notes;             // Ghi chú riêng cho item này
    private LocalDateTime createdDate;

    public void validate() {
        if (stockInTransactionId == null) {
            throw new IllegalArgumentException("Phiếu nhập không được để trống");
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
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Đơn giá không được âm");
        }
    }

    public void calculateTotalAmount() {
        if (quantity != null && unitPrice != null) {
            this.totalAmount = quantity.multiply(unitPrice);
        }
    }
}
