package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.infrastructure.persistence.entity.MaterialCategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MaterialCategoryJpaRepository extends JpaRepository<MaterialCategoryJpaEntity, UUID> {
    Optional<MaterialCategoryJpaEntity> findByCode(String code);
    org.springframework.data.domain.Page<MaterialCategoryJpaEntity> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code, org.springframework.data.domain.Pageable pageable);
    long countByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);
}
