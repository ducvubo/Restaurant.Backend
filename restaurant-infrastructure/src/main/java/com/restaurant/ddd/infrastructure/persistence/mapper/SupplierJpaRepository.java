package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.SupplierJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for Supplier
 */
@Repository
public interface SupplierJpaRepository extends JpaRepository<SupplierJpaEntity, UUID> {
    
    Optional<SupplierJpaEntity> findByCode(String code);
    
    List<SupplierJpaEntity> findByStatus(DataStatus status);
    
    boolean existsByCode(String code);
    
    boolean existsByEmail(String email);
}
