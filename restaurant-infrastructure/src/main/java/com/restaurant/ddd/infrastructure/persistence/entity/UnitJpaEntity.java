package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * JPA Entity for Unit
 */
@Entity
@Table(name = "UNITS")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UnitJpaEntity extends BaseJpaEntity {

    @Column(name = "CODE", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "SYMBOL", length = 20)
    private String symbol;

    @Column(name = "BASE_UNIT_ID", columnDefinition = "UUID")
    private UUID baseUnitId;

    @Column(name = "CONVERSION_RATE", precision = 15, scale = 4)
    private BigDecimal conversionRate;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;
}
