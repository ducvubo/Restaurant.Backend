package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "INVENTORY_COUNT_ITEMS")
public class InventoryCountItemJpaEntity {

    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(name = "INVENTORY_COUNT_ID", nullable = false)
    private UUID inventoryCountId;

    @Column(name = "MATERIAL_ID", nullable = false)
    private UUID materialId;

    @Column(name = "UNIT_ID", nullable = false)
    private UUID unitId;

    @Column(name = "INVENTORY_LEDGER_ID", nullable = false)
    private UUID inventoryLedgerId;

    @Column(name = "BATCH_NUMBER", length = 50)
    private String batchNumber;

    @Column(name = "TRANSACTION_DATE")
    private LocalDateTime transactionDate;

    @Column(name = "SYSTEM_QUANTITY", nullable = false, precision = 15, scale = 3)
    private BigDecimal systemQuantity;

    @Column(name = "ACTUAL_QUANTITY", nullable = false, precision = 15, scale = 3)
    private BigDecimal actualQuantity;

    @Column(name = "DIFFERENCE_QUANTITY", precision = 15, scale = 3)
    private BigDecimal differenceQuantity;

    @Column(name = "NOTES", length = 500)
    private String notes;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;
}
