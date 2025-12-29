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
 * JPA Entity for PurchaseRequisitionItem - Chi tiết yêu cầu mua hàng
 */
@Entity
@Table(name = "PURCHASE_REQUISITION_ITEMS")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PurchaseRequisitionItemJpaEntity extends BaseJpaEntity {

    @Column(name = "REQUISITION_ID", nullable = false)
    private UUID requisitionId;

    @Column(name = "MATERIAL_ID", nullable = false)
    private UUID materialId;

    @Column(name = "QUANTITY", nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;

    @Column(name = "UNIT_ID", nullable = false)
    private UUID unitId;

    @Column(name = "ESTIMATED_PRICE", precision = 18, scale = 2)
    private BigDecimal estimatedPrice;

    @Column(name = "ESTIMATED_AMOUNT", precision = 18, scale = 2)
    private BigDecimal estimatedAmount;

    @Column(name = "NOTES", length = 200)
    private String notes;
}
