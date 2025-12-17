package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "ADJUSTMENT_ITEMS")
public class AdjustmentItemJpaEntity {

    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(name = "ADJUSTMENT_TRANSACTION_ID", nullable = false)
    private UUID adjustmentTransactionId;

    @Column(name = "MATERIAL_ID", nullable = false)
    private UUID materialId;

    @Column(name = "UNIT_ID", nullable = false)
    private UUID unitId;

    @Column(name = "QUANTITY", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(name = "NOTES", length = 500)
    private String notes;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;
}
