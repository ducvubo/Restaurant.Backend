package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.infrastructure.persistence.entity.AdjustmentTransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdjustmentTransactionJpaRepository extends JpaRepository<AdjustmentTransactionJpaEntity, UUID> {
}
