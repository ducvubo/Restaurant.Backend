package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.AdjustmentBatchMapping;
import com.restaurant.ddd.infrastructure.persistence.entity.AdjustmentBatchMappingJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AdjustmentBatchMappingDataAccessMapper {
    
    public AdjustmentBatchMappingJpaEntity toEntity(AdjustmentBatchMapping domain) {
        if (domain == null) return null;
        
        AdjustmentBatchMappingJpaEntity entity = new AdjustmentBatchMappingJpaEntity();
        entity.setId(domain.getId());
        entity.setAdjustmentItemId(domain.getAdjustmentItemId());
        entity.setInventoryLedgerId(domain.getInventoryLedgerId());
        entity.setQuantityUsed(domain.getQuantityUsed());
        return entity;
    }
    
    public AdjustmentBatchMapping toDomain(AdjustmentBatchMappingJpaEntity entity) {
        if (entity == null) return null;
        
        AdjustmentBatchMapping domain = new AdjustmentBatchMapping();
        domain.setId(entity.getId());
        domain.setAdjustmentItemId(entity.getAdjustmentItemId());
        domain.setInventoryLedgerId(entity.getInventoryLedgerId());
        domain.setQuantityUsed(entity.getQuantityUsed());
        return domain;
    }
}
