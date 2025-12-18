package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.InventoryCountItem;
import com.restaurant.ddd.infrastructure.persistence.entity.InventoryCountItemJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class InventoryCountItemDataAccessMapper {
    
    public InventoryCountItemJpaEntity toEntity(InventoryCountItem domain) {
        if (domain == null) return null;
        
        InventoryCountItemJpaEntity entity = new InventoryCountItemJpaEntity();
        entity.setId(domain.getId());
        entity.setInventoryCountId(domain.getInventoryCountId());
        entity.setMaterialId(domain.getMaterialId());
        entity.setUnitId(domain.getUnitId());
        entity.setInventoryLedgerId(domain.getInventoryLedgerId());
        entity.setBatchNumber(domain.getBatchNumber());
        entity.setTransactionDate(domain.getTransactionDate());
        entity.setSystemQuantity(domain.getSystemQuantity());
        entity.setActualQuantity(domain.getActualQuantity());
        entity.setDifferenceQuantity(domain.getDifferenceQuantity());
        entity.setNotes(domain.getNotes());
        entity.setCreatedDate(domain.getCreatedDate());
        return entity;
    }
    
    public InventoryCountItem toDomain(InventoryCountItemJpaEntity entity) {
        if (entity == null) return null;
        
        InventoryCountItem domain = new InventoryCountItem();
        domain.setId(entity.getId());
        domain.setInventoryCountId(entity.getInventoryCountId());
        domain.setMaterialId(entity.getMaterialId());
        domain.setUnitId(entity.getUnitId());
        domain.setInventoryLedgerId(entity.getInventoryLedgerId());
        domain.setBatchNumber(entity.getBatchNumber());
        domain.setTransactionDate(entity.getTransactionDate());
        domain.setSystemQuantity(entity.getSystemQuantity());
        domain.setActualQuantity(entity.getActualQuantity());
        domain.setDifferenceQuantity(entity.getDifferenceQuantity());
        domain.setNotes(entity.getNotes());
        domain.setCreatedDate(entity.getCreatedDate());
        return domain;
    }
}
