package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.MaterialJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaterialJpaRepository extends JpaRepository<MaterialJpaEntity, UUID> {
    Optional<MaterialJpaEntity> findByCode(String code);
    List<MaterialJpaEntity> findByStatus(DataStatus status);
    List<MaterialJpaEntity> findByCategory(String category);
}
