package com.restaurant.ddd.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * StockOutBatchMapping - Truy vết xuất kho từ batch nhập nào
 * Ghi lại: Mỗi item xuất lấy từ phiếu nhập nào, số lượng bao nhiêu
 */
@Data
@Accessors(chain = true)
public class StockOutBatchMapping {
    private UUID id;
    private UUID stockOutItemId;        // FK to StockOutItem
    private UUID inventoryLedgerId;     // FK to InventoryLedger (batch nhập)
    private BigDecimal quantityUsed;    // Số lượng lấy từ batch này
    private BigDecimal unitPrice;       // Đơn giá của batch này
    private LocalDateTime createdDate;

    public void validate() {
        if (stockOutItemId == null) {
            throw new IllegalArgumentException("Stock out item không được để trống");
        }
        if (inventoryLedgerId == null) {
            throw new IllegalArgumentException("Inventory ledger không được để trống");
        }
        if (quantityUsed == null || quantityUsed.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
    }
}
