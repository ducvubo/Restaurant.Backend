package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "ADJUSTMENT_BATCH_MAPPINGS")
public class AdjustmentBatchMappingJpaEntity {

    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(name = "ADJUSTMENT_ITEM_ID", nullable = false)
    private UUID adjustmentItemId;

    @Column(name = "INVENTORY_LEDGER_ID", nullable = false)
    private UUID inventoryLedgerId;

    @Column(name = "QUANTITY_USED", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantityUsed;
}
