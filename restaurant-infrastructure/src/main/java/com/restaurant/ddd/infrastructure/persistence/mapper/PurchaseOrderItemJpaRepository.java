package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.infrastructure.persistence.entity.PurchaseOrderItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository for PurchaseOrderItem
 */
@Repository
public interface PurchaseOrderItemJpaRepository extends JpaRepository<PurchaseOrderItemJpaEntity, UUID> {
    
    List<PurchaseOrderItemJpaEntity> findByPoId(UUID poId);
    
    void deleteByPoId(UUID poId);
}
