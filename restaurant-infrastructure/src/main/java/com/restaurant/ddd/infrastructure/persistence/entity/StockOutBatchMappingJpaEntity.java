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
 * Entity cho bảng STOCK_OUT_BATCH_MAPPING
 * Truy vết: Item xuất lấy từ batch nhập nào
 */
@Entity
@Table(name = "STOCK_OUT_BATCH_MAPPING")
@Data
@EqualsAndHashCode(callSuper = false)
public class StockOutBatchMappingJpaEntity {
    
    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(name = "STOCK_OUT_ITEM_ID", nullable = false)
    private UUID stockOutItemId;

    @Column(name = "INVENTORY_LEDGER_ID", nullable = false)
    private UUID inventoryLedgerId;

    @Column(name = "QUANTITY_USED", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantityUsed;

    @Column(name = "UNIT_PRICE", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;
}
