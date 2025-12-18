package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.inventorycount.*;

import java.util.List;
import java.util.UUID;

/**
 * Application Service for Inventory Count operations
 */
public interface InventoryCountAppService {
    
    /**
     * Create a new inventory count
     */
    InventoryCountDTO create(InventoryCountRequest request);
    
    /**
     * Update an existing inventory count
     */
    InventoryCountDTO update(UUID id, InventoryCountRequest request);
    
    /**
     * Get inventory count by ID
     */
    InventoryCountDTO get(UUID id);
    
    /**
     * List inventory counts with filters and pagination
     */
    InventoryCountListResponse list(InventoryCountListRequest request);
    
    /**
     * Delete inventory count
     */
    void delete(UUID id);
    
    /**
     * Load available batches for a warehouse to start counting
     */
    List<BatchInfoDTO> loadBatchesForCount(UUID warehouseId);
    
    /**
     * Complete inventory count and create adjustment transaction
     */
    InventoryCountDTO complete(UUID id);
    
    /**
     * Cancel inventory count
     */
    InventoryCountDTO cancel(UUID id);
}
