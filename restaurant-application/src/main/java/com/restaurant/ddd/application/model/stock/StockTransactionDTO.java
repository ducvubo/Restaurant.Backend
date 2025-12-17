package com.restaurant.ddd.application.model.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for StockTransaction
 */
@Data
public class StockTransactionDTO {
    private UUID id;
    private String transactionCode;
    private UUID warehouseId;
    private String warehouseName;
    private UUID materialId;
    private String materialName;
    private UUID supplierId;
    private String supplierName;
    
    // Stock In specific fields
    private Integer stockInType; // 1=Nhập từ NCC, 2=Chuyển kho nội bộ
    private String stockInTypeName;
    private UUID relatedTransactionId; // Link to source stock-out for internal transfers
    private String relatedTransactionCode;
    
    private Integer transactionType; // Code
    private String transactionTypeName;
    private BigDecimal quantity;
    private UUID unitId;
    private String unitName;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private LocalDateTime transactionDate;
    private String referenceNumber;
    private UUID destinationBranchId;
    private String notes;
    private UUID performedBy;
    private Integer status;
    private UUID createdBy;
    private LocalDateTime createdDate;
    private Boolean isLocked;                      // Đã chốt phiếu hay chưa
    
    // Danh sách items - chỉ có một trong hai
    private List<StockInItemDTO> stockInItems;     // Nếu transactionType = IN
    private List<StockOutItemDTO> stockOutItems;   // Nếu transactionType = OUT
    
    // Stock Out Type specific fields (only for OUT transactions)
    private Integer stockOutType;                  // 1=Transfer, 2=Sale, 3=Disposal
    private String stockOutTypeName;
    private UUID destinationWarehouseId;           // For INTERNAL_TRANSFER
    private String destinationWarehouseName;
    private UUID customerId;                       // For RETAIL_SALE
    private String customerName;
    private String disposalReason;                 // For DISPOSAL
}
