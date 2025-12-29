package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.infrastructure.persistence.entity.PurchaseRequisitionItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository for PurchaseRequisitionItem
 */
@Repository
public interface PurchaseRequisitionItemJpaRepository extends JpaRepository<PurchaseRequisitionItemJpaEntity, UUID> {
    
    List<PurchaseRequisitionItemJpaEntity> findByRequisitionId(UUID requisitionId);
    
    void deleteByRequisitionId(UUID requisitionId);
}
