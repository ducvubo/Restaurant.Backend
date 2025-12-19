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
@Table(name = "STOCK_OUT_TRANSACTIONS")
public class StockOutTransactionJpaEntity extends BaseJpaEntity {
    
    @Column(name = "STOCK_OUT_TYPE", nullable = false)
    private Integer stockOutType;
    
    @Column(name = "DESTINATION_WAREHOUSE_ID")
    private UUID destinationWarehouseId;
    
    @Column(name = "CUSTOMER_ID")
    private UUID customerId;
    
    @Column(name = "DISPOSAL_REASON", length = 500)
    private String disposalReason;

    @Column(name = "TRANSACTION_CODE", nullable = false, unique = true)
    private String transactionCode;

    @Column(name = "WAREHOUSE_ID", nullable = false)
    private UUID warehouseId;

    @Column(name = "DESTINATION_BRANCH_ID")
    private UUID destinationBranchId;

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

    @Column(name = "ISSUED_BY")
    private UUID issuedBy;

    @Column(name = "RECEIVED_BY")
    private UUID receivedBy;

    @Column(name = "IS_LOCKED")
    private Boolean isLocked;
}
