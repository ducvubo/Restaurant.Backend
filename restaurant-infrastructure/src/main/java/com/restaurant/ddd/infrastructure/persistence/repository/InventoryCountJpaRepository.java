package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.infrastructure.persistence.entity.InventoryCountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryCountJpaRepository extends JpaRepository<InventoryCountJpaEntity, UUID>, 
                                                      JpaSpecificationExecutor<InventoryCountJpaEntity> {
    Optional<InventoryCountJpaEntity> findByCountCode(String countCode);
}
