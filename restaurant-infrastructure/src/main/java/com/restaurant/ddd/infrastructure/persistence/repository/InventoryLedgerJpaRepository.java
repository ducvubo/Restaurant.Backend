package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.domain.enums.InventoryMethod;
import com.restaurant.ddd.infrastructure.persistence.entity.InventoryLedgerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface InventoryLedgerJpaRepository extends 
        JpaRepository<InventoryLedgerJpaEntity, UUID>,
        JpaSpecificationExecutor<InventoryLedgerJpaEntity> {
    
    List<InventoryLedgerJpaEntity> findByWarehouseIdAndMaterialId(UUID warehouseId, UUID materialId);
    
    @Query("SELECT i FROM InventoryLedgerJpaEntity i " +
           "WHERE i.warehouseId = :warehouseId " +
           "AND i.materialId = :materialId " +
           "AND i.inventoryMethod = :inventoryMethod " +
           "AND i.remainingQuantity > 0 " +
           "ORDER BY i.transactionDate ASC, i.createdDate ASC, i.id ASC")
    List<InventoryLedgerJpaEntity> findByWarehouseIdAndMaterialIdAndInventoryMethod(
            @Param("warehouseId") UUID warehouseId, 
            @Param("materialId") UUID materialId, 
            @Param("inventoryMethod") InventoryMethod inventoryMethod);

    List<InventoryLedgerJpaEntity> findByTransactionId(UUID transactionId);
    
    @Query("SELECT SUM(i.remainingQuantity) FROM InventoryLedgerJpaEntity i " +
           "WHERE i.warehouseId = :warehouseId AND i.materialId = :materialId")
    BigDecimal sumQuantityByWarehouseAndMaterial(
            @Param("warehouseId") UUID warehouseId, 
            @Param("materialId") UUID materialId);
    
    @Query("SELECT i FROM InventoryLedgerJpaEntity i " +
           "WHERE i.warehouseId = :warehouseId " +
           "AND i.remainingQuantity > :minQuantity " +
           "ORDER BY i.materialId, i.transactionDate ASC")
    List<InventoryLedgerJpaEntity> findByWarehouseIdAndRemainingQuantityGreaterThan(
            @Param("warehouseId") UUID warehouseId,
            @Param("minQuantity") BigDecimal minQuantity);
    
    @Query("SELECT COUNT(i) FROM InventoryLedgerJpaEntity i " +
           "WHERE i.materialId = :materialId AND (i.originalUnitId = :unitId OR i.baseUnitId = :unitId OR i.unitId = :unitId)")
    long countByMaterialIdAndOriginalUnitId(
            @Param("materialId") UUID materialId,
            @Param("unitId") UUID unitId);
}
