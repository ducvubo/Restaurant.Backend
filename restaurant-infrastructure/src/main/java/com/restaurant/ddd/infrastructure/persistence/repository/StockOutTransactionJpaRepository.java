package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.infrastructure.persistence.entity.StockOutTransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface StockOutTransactionJpaRepository extends 
        JpaRepository<StockOutTransactionJpaEntity, UUID>,
        JpaSpecificationExecutor<StockOutTransactionJpaEntity> {
}
