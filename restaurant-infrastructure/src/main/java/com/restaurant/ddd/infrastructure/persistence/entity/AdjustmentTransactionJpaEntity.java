package com.restaurant.ddd.infrastructure.persistence.entity;

import com.restaurant.ddd.domain.enums.AdjustmentType;
import com.restaurant.ddd.domain.enums.DataStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "ADJUSTMENT_TRANSACTIONS")
public class AdjustmentTransactionJpaEntity extends BaseJpaEntity {

    @Column(name = "TRANSACTION_CODE", nullable = false, unique = true)
    private String transactionCode;

    @Column(name = "WAREHOUSE_ID", nullable = false)
    private UUID warehouseId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "ADJUSTMENT_TYPE", nullable = false)
    private AdjustmentType adjustmentType;

    @Column(name = "TRANSACTION_DATE", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "REASON", nullable = false, length = 500)
    private String reason;

    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;

    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "IS_LOCKED")
    private Boolean isLocked;

    @Column(name = "PERFORMED_BY")
    private UUID performedBy;
}
