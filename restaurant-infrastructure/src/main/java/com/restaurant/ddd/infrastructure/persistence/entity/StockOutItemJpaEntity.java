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
 * StockOutItemJpaEntity - Entity cho chi tiết phiếu xuất kho
 */
@Entity
@Table(name = "STOCK_OUT_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class StockOutItemJpaEntity {
    
    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(name = "STOCK_OUT_TRANSACTION_ID", nullable = false)
    private UUID stockOutTransactionId;

    @Column(name = "MATERIAL_ID", nullable = false)
    private UUID materialId;

    @Column(name = "UNIT_ID", nullable = false)
    private UUID unitId;

    @Column(name = "QUANTITY", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(name = "UNIT_PRICE", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "TOTAL_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "NOTES", length = 500)
    private String notes;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;
}
