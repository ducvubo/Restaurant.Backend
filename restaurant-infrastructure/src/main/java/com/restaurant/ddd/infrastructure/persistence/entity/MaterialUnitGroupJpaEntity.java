package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "material_unit_group")
public class MaterialUnitGroupJpaEntity extends BaseJpaEntity {

    @Column(name = "material_id", nullable = false, columnDefinition = "UUID")
    private UUID materialId;

    @Column(name = "unit_id", nullable = false, columnDefinition = "UUID")
    private UUID unitId;

    @Column(name = "is_base_unit", nullable = false)
    private Boolean isBaseUnit = false;
}
