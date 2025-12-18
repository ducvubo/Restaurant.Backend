package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.infrastructure.persistence.entity.StockInTransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface StockInTransactionJpaRepository extends 
        JpaRepository<StockInTransactionJpaEntity, UUID>,
        JpaSpecificationExecutor<StockInTransactionJpaEntity> {
}
