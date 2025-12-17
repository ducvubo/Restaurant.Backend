package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.infrastructure.persistence.entity.StockOutTransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StockOutTransactionJpaRepository extends JpaRepository<StockOutTransactionJpaEntity, UUID> {
}
