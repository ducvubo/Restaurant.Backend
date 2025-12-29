package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.PurchaseRequisitionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for PurchaseRequisition
 */
@Repository
public interface PurchaseRequisitionJpaRepository extends 
        JpaRepository<PurchaseRequisitionJpaEntity, UUID>,
        JpaSpecificationExecutor<PurchaseRequisitionJpaEntity> {
    
    Optional<PurchaseRequisitionJpaEntity> findByRequisitionCode(String code);
    
    List<PurchaseRequisitionJpaEntity> findByStatus(DataStatus status);
    
    List<PurchaseRequisitionJpaEntity> findByWarehouseId(UUID warehouseId);
    
    boolean existsByRequisitionCode(String code);
}
