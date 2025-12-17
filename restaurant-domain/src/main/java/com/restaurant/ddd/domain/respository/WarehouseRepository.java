package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Warehouse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository {
    Warehouse save(Warehouse warehouse);
    Optional<Warehouse> findById(UUID id);
    List<Warehouse> findAll();
    List<Warehouse> findByStatus(DataStatus status);
    Optional<Warehouse> findByCode(String code);
    List<Warehouse> findByBranch(UUID branchId);
}
