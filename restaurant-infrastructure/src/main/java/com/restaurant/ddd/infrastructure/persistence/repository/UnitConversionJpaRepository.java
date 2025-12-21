package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.UnitConversionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UnitConversionJpaRepository extends JpaRepository<UnitConversionJpaEntity, UUID>,
        org.springframework.data.jpa.repository.JpaSpecificationExecutor<UnitConversionJpaEntity> {
    
    Optional<UnitConversionJpaEntity> findByFromUnitIdAndToUnitId(UUID fromUnitId, UUID toUnitId);
    
    List<UnitConversionJpaEntity> findByFromUnitIdAndStatus(UUID fromUnitId, DataStatus status);
    
    List<UnitConversionJpaEntity> findByStatus(DataStatus status);
    
    @Query("SELECT COUNT(il) FROM InventoryLedgerJpaEntity il " +
           "WHERE (il.originalUnitId = :fromUnitId AND il.baseUnitId = :toUnitId) " +
           "OR (il.originalUnitId = :toUnitId AND il.baseUnitId = :fromUnitId)")
    long countUsageInLedger(@Param("fromUnitId") UUID fromUnitId, 
                           @Param("toUnitId") UUID toUnitId);
}
