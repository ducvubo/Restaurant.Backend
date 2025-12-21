package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.MaterialUnitGroupJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaterialUnitGroupJpaRepository extends JpaRepository<MaterialUnitGroupJpaEntity, UUID> {
    
    List<MaterialUnitGroupJpaEntity> findByMaterialIdAndStatus(UUID materialId, DataStatus status);
    
    Optional<MaterialUnitGroupJpaEntity> findByMaterialIdAndIsBaseUnitTrueAndStatus(UUID materialId, DataStatus status);
    
    boolean existsByMaterialIdAndUnitIdAndStatus(UUID materialId, UUID unitId, DataStatus status);
    
    @Query("SELECT COUNT(il) FROM InventoryLedgerJpaEntity il WHERE il.materialId = :materialId")
    long countTransactionsByMaterial(@Param("materialId") UUID materialId);
    
    
    long countByUnitIdAndStatus(UUID unitId, DataStatus status);
}
