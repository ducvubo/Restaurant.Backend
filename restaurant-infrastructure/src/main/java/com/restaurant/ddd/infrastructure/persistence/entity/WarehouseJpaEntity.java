package com.restaurant.ddd.infrastructure.persistence.entity;

import com.restaurant.ddd.domain.enums.WarehouseType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "WAREHOUSES")
public class WarehouseJpaEntity extends BaseJpaEntity {

    @Column(name = "CODE", nullable = false, unique = true)
    private String code;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "BRANCH_ID")
    private UUID branchId;

    @Column(name = "ADDRESS", columnDefinition = "TEXT")
    private String address;

    @Column(name = "CAPACITY")
    private BigDecimal capacity;

    @Column(name = "MANAGER_ID")
    private UUID managerId;

    @Column(name = "WAREHOUSE_TYPE")
    @Enumerated(EnumType.STRING)
    private WarehouseType warehouseType;
}
