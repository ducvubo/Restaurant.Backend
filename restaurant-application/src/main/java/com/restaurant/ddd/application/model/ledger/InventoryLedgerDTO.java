package com.restaurant.ddd.application.model.ledger;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for InventoryLedger
 */
@Data
public class InventoryLedgerDTO {
    private UUID id;
    private UUID warehouseId;
    private String warehouseName;
    private UUID materialId;
    private String materialName;
    private UUID transactionId;
    private String transactionCode;
    private LocalDateTime transactionDate;
    private String inventoryMethod;  // FIFO, LIFO
    private BigDecimal quantity;  // Số lượng nhập ban đầu
    private BigDecimal quantityOut;  // Số lượng đã xuất (calculated from quantity - remainingQuantity)
    private UUID unitId;
    private String unitName;
    private BigDecimal unitPrice;
    private BigDecimal remainingQuantity;  // Số lượng còn lại
    private String batchNumber;
    private LocalDateTime createdDate;
}
