package com.restaurant.ddd.infrastructure.persistence.entity;

import com.restaurant.ddd.domain.enums.InventoryCountStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "INVENTORY_COUNTS")
public class InventoryCountJpaEntity extends BaseJpaEntity {

    @Column(name = "COUNT_CODE", nullable = false, unique = true, length = 50)
    private String countCode;

    @Column(name = "WAREHOUSE_ID", nullable = false)
    private UUID warehouseId;

    @Column(name = "COUNT_DATE", nullable = false)
    private LocalDateTime countDate;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "COUNT_STATUS", nullable = false)
    private InventoryCountStatus countStatus;

    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "ADJUSTMENT_TRANSACTION_ID")
    private UUID adjustmentTransactionId;

    @Column(name = "PERFORMED_BY")
    private UUID performedBy;
}
