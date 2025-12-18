package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.InventoryCount;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for InventoryCount
 */
public interface InventoryCountRepository {
    InventoryCount save(InventoryCount inventoryCount);
    Optional<InventoryCount> findById(UUID id);
    Optional<InventoryCount> findByCountCode(String countCode);
    void delete(UUID id);
}
