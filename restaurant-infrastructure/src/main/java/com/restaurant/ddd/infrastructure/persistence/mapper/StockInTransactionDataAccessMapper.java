package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.StockInTransaction;
import com.restaurant.ddd.infrastructure.persistence.entity.StockInTransactionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class StockInTransactionDataAccessMapper {

    public StockInTransactionJpaEntity toEntity(StockInTransaction domain) {
        if (domain == null) return null;
        
        StockInTransactionJpaEntity entity = new StockInTransactionJpaEntity();
        entity.setId(domain.getId());
        entity.setTransactionCode(domain.getTransactionCode());
        entity.setWarehouseId(domain.getWarehouseId());
        entity.setSupplierId(domain.getSupplierId());
        entity.setTotalAmount(domain.getTotalAmount());
        entity.setTransactionDate(domain.getTransactionDate());
        entity.setReferenceNumber(domain.getReferenceNumber());
        entity.setNotes(domain.getNotes());
        entity.setPerformedBy(domain.getPerformedBy());
        entity.setReceivedBy(domain.getReceivedBy());
        entity.setStockInType(domain.getStockInType());
        entity.setRelatedTransactionId(domain.getRelatedTransactionId());
        entity.setIsLocked(domain.getIsLocked());
        entity.setStatus(domain.getStatus());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setCreatedDate(domain.getCreatedDate());
        entity.setUpdatedDate(domain.getUpdatedDate());
        return entity;
    }

    public StockInTransaction toDomain(StockInTransactionJpaEntity entity) {
        if (entity == null) return null;
        
        return new StockInTransaction()
                .setId(entity.getId())
                .setTransactionCode(entity.getTransactionCode())
                .setWarehouseId(entity.getWarehouseId())
                .setSupplierId(entity.getSupplierId())
                .setTotalAmount(entity.getTotalAmount())
                .setTransactionDate(entity.getTransactionDate())
                .setReferenceNumber(entity.getReferenceNumber())
                .setNotes(entity.getNotes())
                .setPerformedBy(entity.getPerformedBy())
                .setReceivedBy(entity.getReceivedBy())
                .setStockInType(entity.getStockInType())
                .setRelatedTransactionId(entity.getRelatedTransactionId())
                .setIsLocked(entity.getIsLocked())
                .setStatus(entity.getStatus())
                .setCreatedBy(entity.getCreatedBy())
                .setUpdatedBy(entity.getUpdatedBy())
                .setCreatedDate(entity.getCreatedDate())
                .setUpdatedDate(entity.getUpdatedDate());
    }
}
