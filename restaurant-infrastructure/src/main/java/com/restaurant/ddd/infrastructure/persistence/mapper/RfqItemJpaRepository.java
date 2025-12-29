package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.infrastructure.persistence.entity.RfqItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository for RfqItem
 */
@Repository
public interface RfqItemJpaRepository extends JpaRepository<RfqItemJpaEntity, UUID> {
    
    List<RfqItemJpaEntity> findByRfqId(UUID rfqId);
    
    void deleteByRfqId(UUID rfqId);
}
