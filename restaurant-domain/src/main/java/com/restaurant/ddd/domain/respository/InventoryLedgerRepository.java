package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.enums.InventoryMethod;
import com.restaurant.ddd.domain.model.InventoryLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryLedgerRepository {
    InventoryLedger save(InventoryLedger ledger);
    Optional<InventoryLedger> findById(UUID id);
    List<InventoryLedger> findByWarehouseAndMaterial(UUID warehouseId, UUID materialId);
    BigDecimal getCurrentStock(UUID warehouseId, UUID materialId);
    List<InventoryLedger> findAvailableBatches(UUID warehouseId, UUID materialId, InventoryMethod method);
    List<InventoryLedger> findAvailableStock(UUID warehouseId, UUID materialId);
    List<InventoryLedger> findByTransactionId(UUID transactionId);
    void delete(InventoryLedger ledger);
    
    /**
     * Find all inventory ledgers with filters and pagination
     */
    Page<InventoryLedger> findAll(
        UUID warehouseId,
        UUID materialId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );
    
    /**
     * Count ledger entries by material and original unit
     */
    long countByMaterialIdAndOriginalUnitId(UUID materialId, UUID unitId);
}
