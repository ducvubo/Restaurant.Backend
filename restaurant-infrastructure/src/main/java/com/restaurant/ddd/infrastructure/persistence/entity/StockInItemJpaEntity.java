package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * StockInItemJpaEntity - Entity cho chi tiết phiếu nhập kho
 */
@Entity
@Table(name = "STOCK_IN_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class StockInItemJpaEntity {
    
    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(name = "STOCK_IN_TRANSACTION_ID", nullable = false)
    private UUID stockInTransactionId;

    @Column(name = "MATERIAL_ID", nullable = false)
    private UUID materialId;

    @Column(name = "UNIT_ID", nullable = false)
    private UUID unitId;

    @Column(name = "QUANTITY", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(name = "UNIT_PRICE", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "TOTAL_AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "NOTES", length = 500)
    private String notes;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;
}
