package com.restaurant.ddd.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * StockOutItem - Chi tiết phiếu xuất kho
 * Đại diện cho một dòng nguyên liệu trong phiếu xuất
 */
@Data
@Accessors(chain = true)
public class StockOutItem {
    private UUID id;
    private UUID stockOutTransactionId;  // FK to STOCK_OUT_TRANSACTIONS
    private UUID materialId;
    private UUID unitId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;     // Optional - giá tham khảo
    private BigDecimal totalAmount;   // Optional - giá trị tham khảo
    private String notes;             // Ghi chú riêng cho item này
    private LocalDateTime createdDate;

    public void validate() {
        if (stockOutTransactionId == null) {
            throw new IllegalArgumentException("Phiếu xuất không được để trống");
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

    public void calculateTotalAmount() {
        if (quantity != null && unitPrice != null) {
            this.totalAmount = quantity.multiply(unitPrice);
        }
    }
}
