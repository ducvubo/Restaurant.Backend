package com.restaurant.ddd.infrastructure.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "MATERIALS")
public class MaterialJpaEntity extends BaseJpaEntity {

    @Column(name = "CODE", nullable = false, unique = true)
    private String code;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "CATEGORY")
    private String category;

    @Column(name = "CATEGORY_ID")
    private UUID categoryId;

    @Column(name = "UNIT_ID")
    private UUID unitId;

    @Column(name = "UNIT_PRICE")
    private BigDecimal unitPrice;

    @Column(name = "MIN_STOCK_LEVEL")
    private BigDecimal minStockLevel;

    @Column(name = "MAX_STOCK_LEVEL")
    private BigDecimal maxStockLevel;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;
}
