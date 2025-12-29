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
 * JPA Entity for RfqItem - Chi tiết yêu cầu báo giá
 */
@Entity
@Table(name = "RFQ_ITEMS")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RfqItemJpaEntity extends BaseJpaEntity {

    @Column(name = "RFQ_ID", nullable = false)
    private UUID rfqId;

    @Column(name = "MATERIAL_ID", nullable = false)
    private UUID materialId;

    @Column(name = "QUANTITY", nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;

    @Column(name = "UNIT_ID", nullable = false)
    private UUID unitId;

    @Column(name = "UNIT_PRICE", precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "AMOUNT", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "NOTES", length = 200)
    private String notes;
}
