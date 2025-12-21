package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.infrastructure.persistence.entity.UnitConversionHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UnitConversionHistoryJpaRepository extends JpaRepository<UnitConversionHistoryJpaEntity, UUID> {
    
    List<UnitConversionHistoryJpaEntity> findByUnitConversionIdOrderByChangedDateDesc(UUID unitConversionId);
    
    List<UnitConversionHistoryJpaEntity> findByChangedByOrderByChangedDateDesc(UUID changedBy);
}
