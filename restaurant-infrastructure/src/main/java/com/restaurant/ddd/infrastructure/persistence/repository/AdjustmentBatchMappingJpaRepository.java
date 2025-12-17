package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.infrastructure.persistence.entity.AdjustmentBatchMappingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AdjustmentBatchMappingJpaRepository extends JpaRepository<AdjustmentBatchMappingJpaEntity, UUID> {
    List<AdjustmentBatchMappingJpaEntity> findByAdjustmentItemId(UUID itemId);
}
