package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.Warehouse;
import com.restaurant.ddd.infrastructure.persistence.entity.WarehouseJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WarehouseDataAccessMapper {

    public Warehouse warehouseJpaEntityToWarehouse(WarehouseJpaEntity warehouseJpaEntity) {
        if (warehouseJpaEntity == null) {
            return null;
        }
        return new Warehouse()
                .setId(warehouseJpaEntity.getId())
                .setCode(warehouseJpaEntity.getCode())
                .setName(warehouseJpaEntity.getName())
                .setBranchId(warehouseJpaEntity.getBranchId())
                .setAddress(warehouseJpaEntity.getAddress())
                .setCapacity(warehouseJpaEntity.getCapacity())
                .setManagerId(warehouseJpaEntity.getManagerId())
                .setWarehouseType(warehouseJpaEntity.getWarehouseType())
                .setStatus(warehouseJpaEntity.getStatus())
                .setCreatedBy(warehouseJpaEntity.getCreatedBy())
                .setUpdatedBy(warehouseJpaEntity.getUpdatedBy())
                .setCreatedDate(warehouseJpaEntity.getCreatedDate())
                .setUpdatedDate(warehouseJpaEntity.getCreatedDate()); // Using createdDate for now or remove if updatedDate is handled by BaseJpaEntity differently
    }

    public WarehouseJpaEntity warehouseToWarehouseJpaEntity(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        }
        WarehouseJpaEntity entity = new WarehouseJpaEntity();
        if (warehouse.getId() != null) {
            entity.setId(warehouse.getId());
        }
        entity.setCode(warehouse.getCode());
        entity.setName(warehouse.getName());
        entity.setBranchId(warehouse.getBranchId());
        entity.setAddress(warehouse.getAddress());
        entity.setCapacity(warehouse.getCapacity());
        entity.setManagerId(warehouse.getManagerId());
        entity.setWarehouseType(warehouse.getWarehouseType());
        // status is handled by base entity setter if exposed, or explicitly
        entity.setStatus(warehouse.getStatus());
        entity.setCreatedBy(warehouse.getCreatedBy());
        entity.setUpdatedBy(warehouse.getUpdatedBy());
        return entity;
    }

    public List<Warehouse> warehouseJpaEntitiesToWarehouses(List<WarehouseJpaEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream().map(this::warehouseJpaEntityToWarehouse).collect(Collectors.toList());
    }
}
