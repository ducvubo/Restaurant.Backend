package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.enums.InventoryMethod;
import com.restaurant.ddd.domain.model.InventoryLedger;

import java.math.BigDecimal;
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
}
