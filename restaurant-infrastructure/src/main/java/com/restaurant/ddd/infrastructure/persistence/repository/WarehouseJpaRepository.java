package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.WarehouseType;
import com.restaurant.ddd.infrastructure.persistence.entity.WarehouseJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseJpaRepository extends JpaRepository<WarehouseJpaEntity, UUID> {
    Optional<WarehouseJpaEntity> findByCode(String code);
    List<WarehouseJpaEntity> findByStatus(DataStatus status);
    List<WarehouseJpaEntity> findByBranchId(UUID branchId);
    List<WarehouseJpaEntity> findByWarehouseType(WarehouseType type);
}
