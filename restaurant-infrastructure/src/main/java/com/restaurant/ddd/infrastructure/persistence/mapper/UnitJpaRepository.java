package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.UnitJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for Unit
 */
@Repository
public interface UnitJpaRepository extends JpaRepository<UnitJpaEntity, UUID> {
    
    Optional<UnitJpaEntity> findByCode(String code);
    
    List<UnitJpaEntity> findByStatus(DataStatus status);
    
    @Query("SELECT u FROM UnitJpaEntity u WHERE u.baseUnitId IS NULL")
    List<UnitJpaEntity> findBaseUnits();
    
    boolean existsByCode(String code);
}
