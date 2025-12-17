package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.infrastructure.persistence.entity.AdjustmentItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AdjustmentItemJpaRepository extends JpaRepository<AdjustmentItemJpaEntity, UUID> {
    List<AdjustmentItemJpaEntity> findByAdjustmentTransactionId(UUID transactionId);
}
