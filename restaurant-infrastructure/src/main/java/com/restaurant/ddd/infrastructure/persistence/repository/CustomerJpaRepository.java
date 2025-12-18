package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.infrastructure.persistence.entity.CustomerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CustomerJpaRepository extends 
        JpaRepository<CustomerJpaEntity, UUID>,
        JpaSpecificationExecutor<CustomerJpaEntity> {
    Optional<CustomerJpaEntity> findByCustomerCode(String customerCode);
}
