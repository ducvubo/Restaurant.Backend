package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.InventoryCountItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for InventoryCountItem
 */
public interface InventoryCountItemRepository {
    InventoryCountItem save(InventoryCountItem item);
    Optional<InventoryCountItem> findById(UUID id);
    List<InventoryCountItem> findByInventoryCountId(UUID inventoryCountId);
    void deleteByInventoryCountId(UUID inventoryCountId);
}
