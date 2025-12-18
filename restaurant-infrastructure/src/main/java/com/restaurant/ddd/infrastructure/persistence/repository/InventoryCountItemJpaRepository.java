package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.infrastructure.persistence.entity.InventoryCountItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryCountItemJpaRepository extends JpaRepository<InventoryCountItemJpaEntity, UUID> {
    List<InventoryCountItemJpaEntity> findByInventoryCountId(UUID inventoryCountId);
    void deleteByInventoryCountId(UUID inventoryCountId);
}
