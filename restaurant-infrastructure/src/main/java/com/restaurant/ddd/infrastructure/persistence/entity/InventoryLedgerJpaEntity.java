package com.restaurant.ddd.infrastructure.persistence.entity;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.InventoryMethod;
import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "INVENTORY_LEDGER")
public class InventoryLedgerJpaEntity {

    @Id
    @Column(name = "ID", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "WAREHOUSE_ID")
    private UUID warehouseId;

    @Column(name = "MATERIAL_ID")
    private UUID materialId;

    @Column(name = "TRANSACTION_ID")
    private UUID transactionId;

    @Column(name = "TRANSACTION_CODE")
    private String transactionCode;

    @Column(name = "TRANSACTION_DATE", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "INVENTORY_METHOD")
    @Enumerated(EnumType.STRING)
    private InventoryMethod inventoryMethod;

    @Column(name = "QUANTITY", precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(name = "UNIT_ID")
    private UUID unitId;

    @Column(name = "UNIT_PRICE", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "REMAINING_QUANTITY", precision = 15, scale = 3)
    private BigDecimal remainingQuantity;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private DataStatus status;

    @Column(name = "BATCH_NUMBER")
    private String batchNumber;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;
}
