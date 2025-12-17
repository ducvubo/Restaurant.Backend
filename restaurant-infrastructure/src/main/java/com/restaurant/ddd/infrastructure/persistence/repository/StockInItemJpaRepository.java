package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.infrastructure.persistence.entity.StockInItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockInItemJpaRepository extends JpaRepository<StockInItemJpaEntity, UUID> {
    List<StockInItemJpaEntity> findByStockInTransactionId(UUID stockInTransactionId);
    void deleteByStockInTransactionId(UUID stockInTransactionId);
}
