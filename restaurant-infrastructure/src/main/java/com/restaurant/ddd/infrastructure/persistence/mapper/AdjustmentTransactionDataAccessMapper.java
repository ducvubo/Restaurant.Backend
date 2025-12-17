package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.AdjustmentTransaction;
import com.restaurant.ddd.infrastructure.persistence.entity.AdjustmentTransactionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AdjustmentTransactionDataAccessMapper {
    
    public AdjustmentTransactionJpaEntity toEntity(AdjustmentTransaction domain) {
        if (domain == null) return null;
        
        AdjustmentTransactionJpaEntity entity = new AdjustmentTransactionJpaEntity();
        entity.setId(domain.getId());
        entity.setTransactionCode(domain.getTransactionCode());
        entity.setWarehouseId(domain.getWarehouseId());
        entity.setAdjustmentType(domain.getAdjustmentType());
        entity.setTransactionDate(domain.getTransactionDate());
        entity.setReason(domain.getReason());
        entity.setReferenceNumber(domain.getReferenceNumber());
        entity.setNotes(domain.getNotes());
        entity.setIsLocked(domain.getIsLocked());
        entity.setStatus(domain.getStatus());
        entity.setPerformedBy(domain.getPerformedBy());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setCreatedDate(domain.getCreatedDate());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setUpdatedDate(domain.getUpdatedDate());
        return entity;
    }
    
    public AdjustmentTransaction toDomain(AdjustmentTransactionJpaEntity entity) {
        if (entity == null) return null;
        
        AdjustmentTransaction domain = new AdjustmentTransaction();
        domain.setId(entity.getId());
        domain.setTransactionCode(entity.getTransactionCode());
        domain.setWarehouseId(entity.getWarehouseId());
        domain.setAdjustmentType(entity.getAdjustmentType());
        domain.setTransactionDate(entity.getTransactionDate());
        domain.setReason(entity.getReason());
        domain.setReferenceNumber(entity.getReferenceNumber());
        domain.setNotes(entity.getNotes());
        domain.setIsLocked(entity.getIsLocked());
        domain.setStatus(entity.getStatus());
        domain.setPerformedBy(entity.getPerformedBy());
        domain.setCreatedBy(entity.getCreatedBy());
        domain.setCreatedDate(entity.getCreatedDate());
        domain.setUpdatedBy(entity.getUpdatedBy());
        domain.setUpdatedDate(entity.getUpdatedDate());
        return domain;
    }
}
