package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * StockInTransaction - Phiếu nhập kho
 */
@Data
@Accessors(chain = true)
public class StockInTransaction {
    private UUID id;
    private String transactionCode;
    private UUID warehouseId;
    private UUID supplierId;
    private Integer stockInType; // 1=Nhập từ NCC, 2=Chuyển kho nội bộ, 3=Nhập từ PO
    private UUID relatedTransactionId; // Link to source stock-out for internal transfers
    private UUID purchaseOrderId; // Link to purchase order when receiving from PO
    private BigDecimal totalAmount;
    private LocalDateTime transactionDate;
    private String referenceNumber;
    private String notes;
    private UUID performedBy;       // Người thực hiện
    private UUID receivedBy;        // Người nhập kho
    private DataStatus status;
    private Boolean isLocked;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public void validate() {
        if (transactionCode == null || transactionCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã giao dịch không được để trống");
        }
        if (warehouseId == null) {
            throw new IllegalArgumentException("Kho không được để trống");
        }
        if (transactionDate == null) {
            throw new IllegalArgumentException("Ngày giao dịch không được để trống");
        }
    }
}
