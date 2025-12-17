package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.infrastructure.persistence.entity.StockOutItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockOutItemJpaRepository extends JpaRepository<StockOutItemJpaEntity, UUID> {
    List<StockOutItemJpaEntity> findByStockOutTransactionId(UUID stockOutTransactionId);
    void deleteByStockOutTransactionId(UUID stockOutTransactionId);
}
