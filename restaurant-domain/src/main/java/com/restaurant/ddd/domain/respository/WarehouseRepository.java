package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    
    /**
     * Find all warehouses with filters and pagination
     */
    Page<Warehouse> findAll(
        String keyword,
        Integer status,
        UUID branchId,
        Integer warehouseType,
        Pageable pageable
    );
}
