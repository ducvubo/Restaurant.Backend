package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "unit_conversion")
public class UnitConversionJpaEntity extends BaseJpaEntity {

    @Column(name = "from_unit_id", nullable = false, columnDefinition = "UUID")
    private UUID fromUnitId;

    @Column(name = "to_unit_id", nullable = false, columnDefinition = "UUID")
    private UUID toUnitId;

    @Column(name = "conversion_factor", nullable = false, precision = 18, scale = 6)
    private BigDecimal conversionFactor;
}
