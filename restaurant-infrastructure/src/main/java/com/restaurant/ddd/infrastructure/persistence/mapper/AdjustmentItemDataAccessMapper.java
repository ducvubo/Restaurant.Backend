package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.AdjustmentItem;
import com.restaurant.ddd.infrastructure.persistence.entity.AdjustmentItemJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AdjustmentItemDataAccessMapper {
    
    public AdjustmentItemJpaEntity toEntity(AdjustmentItem domain) {
        if (domain == null) return null;
        
        AdjustmentItemJpaEntity entity = new AdjustmentItemJpaEntity();
        entity.setId(domain.getId());
        entity.setAdjustmentTransactionId(domain.getAdjustmentTransactionId());
        entity.setMaterialId(domain.getMaterialId());
        entity.setUnitId(domain.getUnitId());
        entity.setQuantity(domain.getQuantity());
        entity.setInventoryLedgerId(domain.getInventoryLedgerId());
        entity.setNotes(domain.getNotes());
        entity.setCreatedDate(domain.getCreatedDate());
        return entity;
    }
    
    public AdjustmentItem toDomain(AdjustmentItemJpaEntity entity) {
        if (entity == null) return null;
        
        AdjustmentItem domain = new AdjustmentItem();
        domain.setId(entity.getId());
        domain.setAdjustmentTransactionId(entity.getAdjustmentTransactionId());
        domain.setMaterialId(entity.getMaterialId());
        domain.setUnitId(entity.getUnitId());
        domain.setQuantity(entity.getQuantity());
        domain.setInventoryLedgerId(entity.getInventoryLedgerId());
        domain.setNotes(entity.getNotes());
        domain.setCreatedDate(entity.getCreatedDate());
        return domain;
    }
}
