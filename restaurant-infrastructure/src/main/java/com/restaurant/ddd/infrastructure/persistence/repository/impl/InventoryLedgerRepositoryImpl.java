package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.enums.InventoryMethod;
import com.restaurant.ddd.domain.model.InventoryLedger;
import com.restaurant.ddd.domain.respository.InventoryLedgerRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.InventoryLedgerJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.InventoryLedgerDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.repository.InventoryLedgerJpaRepository;
import com.restaurant.ddd.infrastructure.persistence.specification.InventoryLedgerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InventoryLedgerRepositoryImpl implements InventoryLedgerRepository {

    private final InventoryLedgerJpaRepository inventoryLedgerJpaRepository;
    private final InventoryLedgerDataAccessMapper inventoryLedgerDataAccessMapper;

    @Override
    public InventoryLedger save(InventoryLedger ledger) {
        InventoryLedgerJpaEntity entity = inventoryLedgerDataAccessMapper.inventoryLedgerToInventoryLedgerJpaEntity(ledger);
        InventoryLedgerJpaEntity savedEntity = inventoryLedgerJpaRepository.save(entity);
        return inventoryLedgerDataAccessMapper.inventoryLedgerJpaEntityToInventoryLedger(savedEntity);
    }

    @Override
    public Optional<InventoryLedger> findById(UUID id) {
        return inventoryLedgerJpaRepository.findById(id)
                .map(inventoryLedgerDataAccessMapper::inventoryLedgerJpaEntityToInventoryLedger);
    }

    @Override
    public List<InventoryLedger> findByWarehouseAndMaterial(UUID warehouseId, UUID materialId) {
        return inventoryLedgerDataAccessMapper.inventoryLedgerJpaEntitiesToInventoryLedgers(
                inventoryLedgerJpaRepository.findByWarehouseIdAndMaterialId(warehouseId, materialId));
    }

    @Override
    public BigDecimal getCurrentStock(UUID warehouseId, UUID materialId) {
        BigDecimal stock = inventoryLedgerJpaRepository.sumQuantityByWarehouseAndMaterial(warehouseId, materialId);
        return stock != null ? stock : BigDecimal.ZERO;
    }

    @Override
    public List<InventoryLedger> findAvailableBatches(UUID warehouseId, UUID materialId, InventoryMethod method) {
        return inventoryLedgerDataAccessMapper.inventoryLedgerJpaEntitiesToInventoryLedgers(
                inventoryLedgerJpaRepository.findByWarehouseIdAndMaterialIdAndInventoryMethod(warehouseId, materialId, method));
    }

    @Override
    public List<InventoryLedger> findAvailableStock(UUID warehouseId, UUID materialId) {
        // FIFO: Find batches with remaining quantity > 0, ordered by date
        return findAvailableBatches(warehouseId, materialId, InventoryMethod.FIFO);
    }

    @Override
    public List<InventoryLedger> findByTransactionId(UUID transactionId) {
        return inventoryLedgerDataAccessMapper.inventoryLedgerJpaEntitiesToInventoryLedgers(
                inventoryLedgerJpaRepository.findByTransactionId(transactionId));
    }

    @Override
    public void delete(InventoryLedger ledger) {
        inventoryLedgerJpaRepository.deleteById(ledger.getId());
    }
    
    @Override
    public Page<InventoryLedger> findAll(
            UUID warehouseId,
            UUID materialId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        
        return inventoryLedgerJpaRepository.findAll(
                InventoryLedgerSpecification.buildSpec(
                        warehouseId, materialId, startDate, endDate
                ),
                pageable
        ).map(inventoryLedgerDataAccessMapper::inventoryLedgerJpaEntityToInventoryLedger);
    }
    
    @Override
    public long countByMaterialIdAndOriginalUnitId(UUID materialId, UUID unitId) {
        return inventoryLedgerJpaRepository.countByMaterialIdAndOriginalUnitId(materialId, unitId);
    }
}
