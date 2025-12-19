package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.StockOutType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * StockOutTransaction - Phiếu xuất kho
 */
@Data
@Accessors(chain = true)
public class StockOutTransaction {
    private UUID id;
    private String transactionCode;
    private UUID warehouseId;
    private UUID destinationBranchId;
    private BigDecimal totalAmount;
    private LocalDateTime transactionDate;
    private String referenceNumber;
    private String notes;
    private UUID performedBy;       // Người thực hiện
    private UUID issuedBy;          // Người xuất kho
    private UUID receivedBy;        // Người tiếp nhận (cho chuyển kho nội bộ)
    private DataStatus status;
    private Boolean isLocked;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    
    // Stock Out Type specific fields
    private StockOutType stockOutType;           // Required: Type of stock out
    private UUID destinationWarehouseId;         // For INTERNAL_TRANSFER
    private UUID customerId;                     // For RETAIL_SALE
    private String disposalReason;               // For DISPOSAL

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
        
        // Validate stock out type
        if (stockOutType == null) {
            throw new IllegalArgumentException("Loại xuất kho không được để trống");
        }
        
        // Type-specific validation
        switch (stockOutType) {
            case INTERNAL_TRANSFER:
                if (destinationWarehouseId == null) {
                    throw new IllegalArgumentException("Kho đích không được để trống khi chuyển kho nội bộ");
                }
                if (destinationWarehouseId.equals(warehouseId)) {
                    throw new IllegalArgumentException("Kho đích phải khác kho nguồn");
                }
                break;
                
            case RETAIL_SALE:
                if (customerId == null) {
                    throw new IllegalArgumentException("Khách hàng không được để trống khi bán lẻ");
                }
                break;
                
            case DISPOSAL:
                if (disposalReason == null || disposalReason.trim().isEmpty()) {
                    throw new IllegalArgumentException("Lý do tiêu hủy không được để trống");
                }
                break;
        }
    }
    
    public void lock() {
        if (this.isLocked != null && this.isLocked) {
            throw new IllegalStateException("Phiếu xuất đã được chốt");
        }
        this.isLocked = true;
    }
}
