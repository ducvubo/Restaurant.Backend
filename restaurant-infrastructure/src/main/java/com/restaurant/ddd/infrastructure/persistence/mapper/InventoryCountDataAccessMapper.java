package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.InventoryCount;
import com.restaurant.ddd.infrastructure.persistence.entity.InventoryCountJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class InventoryCountDataAccessMapper {
    
    public InventoryCountJpaEntity toEntity(InventoryCount domain) {
        if (domain == null) return null;
        
        InventoryCountJpaEntity entity = new InventoryCountJpaEntity();
        entity.setId(domain.getId());
        entity.setCountCode(domain.getCountCode());
        entity.setWarehouseId(domain.getWarehouseId());
        entity.setCountDate(domain.getCountDate());
        entity.setCountStatus(domain.getCountStatus());
        entity.setNotes(domain.getNotes());
        entity.setAdjustmentTransactionId(domain.getAdjustmentTransactionId());
        entity.setPerformedBy(domain.getPerformedBy());
        entity.setStatus(domain.getStatus());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setCreatedDate(domain.getCreatedDate());
        entity.setUpdatedDate(domain.getUpdatedDate());
        return entity;
    }
    
    public InventoryCount toDomain(InventoryCountJpaEntity entity) {
        if (entity == null) return null;
        
        InventoryCount domain = new InventoryCount();
        domain.setId(entity.getId());
        domain.setCountCode(entity.getCountCode());
        domain.setWarehouseId(entity.getWarehouseId());
        domain.setCountDate(entity.getCountDate());
        domain.setCountStatus(entity.getCountStatus());
        domain.setNotes(entity.getNotes());
        domain.setAdjustmentTransactionId(entity.getAdjustmentTransactionId());
        domain.setPerformedBy(entity.getPerformedBy());
        domain.setStatus(entity.getStatus());
        domain.setCreatedBy(entity.getCreatedBy());
        domain.setUpdatedBy(entity.getUpdatedBy());
        domain.setCreatedDate(entity.getCreatedDate());
        domain.setUpdatedDate(entity.getUpdatedDate());
        return domain;
    }
}
