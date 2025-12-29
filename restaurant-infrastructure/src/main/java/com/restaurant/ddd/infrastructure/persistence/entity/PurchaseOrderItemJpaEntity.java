package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * JPA Entity for PurchaseOrderItem - Chi tiết đơn đặt hàng
 */
@Entity
@Table(name = "PURCHASE_ORDER_ITEMS")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PurchaseOrderItemJpaEntity extends BaseJpaEntity {

    @Column(name = "PO_ID", nullable = false)
    private UUID poId;

    @Column(name = "MATERIAL_ID", nullable = false)
    private UUID materialId;

    @Column(name = "QUANTITY", nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;

    @Column(name = "RECEIVED_QUANTITY", precision = 18, scale = 4)
    private BigDecimal receivedQuantity;

    @Column(name = "UNIT_ID", nullable = false)
    private UUID unitId;

    @Column(name = "UNIT_PRICE", nullable = false, precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "AMOUNT", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "NOTES", length = 200)
    private String notes;
}
