package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.InventoryMethod;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * InventoryLedger - Sổ cái tồn kho
 * Domain model for tracking inventory movements (FIFO/LIFO)
 */
@Data
@Accessors(chain = true)
public class InventoryLedger {
    private UUID id;
    private UUID warehouseId;
    private UUID materialId;
    private UUID transactionId;
    private String transactionCode;
    private LocalDateTime transactionDate;
    private InventoryMethod inventoryMethod; // FIFO, LIFO
    private BigDecimal quantity;           // Số lượng nhập
    private UUID unitId;
    private BigDecimal unitPrice;
    private BigDecimal remainingQuantity;  // Số lượng còn lại (cho FIFO)
    private DataStatus status;
    private String batchNumber;
    private LocalDateTime createdDate;

    public void calculateTotalValue() {
        // Helper method if needed
    }
}
