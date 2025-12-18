package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.PolicyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PolicyJpaRepository extends JpaRepository<PolicyJpaEntity, UUID>, JpaSpecificationExecutor<PolicyJpaEntity> {
    
    @Query("SELECT p FROM PolicyJpaEntity p WHERE p.status = :status")
    List<PolicyJpaEntity> findByStatus(@Param("status") com.restaurant.ddd.domain.enums.DataStatus status);
}
