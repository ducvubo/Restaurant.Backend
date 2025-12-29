package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.RfqJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for RFQ
 */
@Repository
public interface RfqJpaRepository extends 
        JpaRepository<RfqJpaEntity, UUID>,
        JpaSpecificationExecutor<RfqJpaEntity> {
    
    Optional<RfqJpaEntity> findByRfqCode(String code);
    
    List<RfqJpaEntity> findByStatus(DataStatus status);
    
    List<RfqJpaEntity> findBySupplierId(UUID supplierId);
    
    List<RfqJpaEntity> findByRequisitionId(UUID requisitionId);
    
    boolean existsByRfqCode(String code);
    
    // Find expired RFQs
    List<RfqJpaEntity> findByValidUntilBeforeAndStatusNot(LocalDateTime now, DataStatus status);
}
