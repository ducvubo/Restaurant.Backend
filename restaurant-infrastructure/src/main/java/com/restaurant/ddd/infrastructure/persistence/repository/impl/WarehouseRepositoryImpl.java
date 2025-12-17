package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Warehouse;
import com.restaurant.ddd.domain.respository.WarehouseRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.WarehouseJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.WarehouseDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.repository.WarehouseJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WarehouseRepositoryImpl implements WarehouseRepository {

    private final WarehouseJpaRepository warehouseJpaRepository;
    private final WarehouseDataAccessMapper warehouseDataAccessMapper;

    @Override
    public Warehouse save(Warehouse warehouse) {
        WarehouseJpaEntity entity = warehouseDataAccessMapper.warehouseToWarehouseJpaEntity(warehouse);
        WarehouseJpaEntity savedEntity = warehouseJpaRepository.save(entity);
        return warehouseDataAccessMapper.warehouseJpaEntityToWarehouse(savedEntity);
    }

    @Override
    public Optional<Warehouse> findById(UUID id) {
        return warehouseJpaRepository.findById(id)
                .map(warehouseDataAccessMapper::warehouseJpaEntityToWarehouse);
    }

    @Override
    public List<Warehouse> findAll() {
        return warehouseDataAccessMapper.warehouseJpaEntitiesToWarehouses(warehouseJpaRepository.findAll());
    }

    @Override
    public List<Warehouse> findByStatus(DataStatus status) {
        return warehouseDataAccessMapper.warehouseJpaEntitiesToWarehouses(warehouseJpaRepository.findByStatus(status));
    }

    @Override
    public Optional<Warehouse> findByCode(String code) {
        return warehouseJpaRepository.findByCode(code)
                .map(warehouseDataAccessMapper::warehouseJpaEntityToWarehouse);
    }

    @Override
    public List<Warehouse> findByBranch(UUID branchId) {
        return warehouseDataAccessMapper.warehouseJpaEntitiesToWarehouses(warehouseJpaRepository.findByBranchId(branchId));
    }
}
