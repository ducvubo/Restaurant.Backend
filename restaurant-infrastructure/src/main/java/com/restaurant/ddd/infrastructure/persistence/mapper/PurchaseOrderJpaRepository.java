package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.PurchaseOrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for PurchaseOrder
 */
@Repository
public interface PurchaseOrderJpaRepository extends 
        JpaRepository<PurchaseOrderJpaEntity, UUID>,
        JpaSpecificationExecutor<PurchaseOrderJpaEntity> {
    
    Optional<PurchaseOrderJpaEntity> findByPoCode(String code);
    
    List<PurchaseOrderJpaEntity> findByStatus(DataStatus status);
    
    List<PurchaseOrderJpaEntity> findBySupplierId(UUID supplierId);
    
    List<PurchaseOrderJpaEntity> findByWarehouseId(UUID warehouseId);
    
    List<PurchaseOrderJpaEntity> findByRfqId(UUID rfqId);
    
    boolean existsByPoCode(String code);
}
