package com.restaurant.ddd.infrastructure.persistence.entity;

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
@Table(name = "STOCK_IN_TRANSACTIONS")
public class StockInTransactionJpaEntity extends BaseJpaEntity {

    @Column(name = "TRANSACTION_CODE", nullable = false, unique = true)
    private String transactionCode;

    @Column(name = "WAREHOUSE_ID", nullable = false)
    private UUID warehouseId;

    @Column(name = "SUPPLIER_ID")
    private UUID supplierId;

    @Column(name = "TOTAL_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "TRANSACTION_DATE", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;

    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "PERFORMED_BY")
    private UUID performedBy;

    @Column(name = "RECEIVED_BY")
    private UUID receivedBy;

    @Column(name = "STOCK_IN_TYPE")
    private Integer stockInType;

    @Column(name = "RELATED_TRANSACTION_ID")
    private UUID relatedTransactionId;

    @Column(name = "PURCHASE_ORDER_ID")
    private UUID purchaseOrderId;

    @Column(name = "IS_LOCKED")
    private Boolean isLocked;
}
