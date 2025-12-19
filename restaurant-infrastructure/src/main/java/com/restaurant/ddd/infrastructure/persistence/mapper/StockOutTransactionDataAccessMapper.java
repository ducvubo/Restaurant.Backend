package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.StockOutTransaction;
import com.restaurant.ddd.infrastructure.persistence.entity.StockOutTransactionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class StockOutTransactionDataAccessMapper {

    public StockOutTransactionJpaEntity toEntity(StockOutTransaction domain) {
        if (domain == null) return null;
        
        StockOutTransactionJpaEntity entity = new StockOutTransactionJpaEntity();
        entity.setId(domain.getId());
        entity.setTransactionCode(domain.getTransactionCode());
        entity.setWarehouseId(domain.getWarehouseId());
        entity.setDestinationBranchId(domain.getDestinationBranchId());
        entity.setTotalAmount(domain.getTotalAmount());
        entity.setTransactionDate(domain.getTransactionDate());
        entity.setReferenceNumber(domain.getReferenceNumber());
        entity.setNotes(domain.getNotes());
        entity.setPerformedBy(domain.getPerformedBy());
        entity.setIssuedBy(domain.getIssuedBy());
        entity.setReceivedBy(domain.getReceivedBy());
        entity.setIsLocked(domain.getIsLocked());
        entity.setStatus(domain.getStatus());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setCreatedDate(domain.getCreatedDate());
        entity.setUpdatedDate(domain.getUpdatedDate());
        // Stock Out Type fields
        entity.setStockOutType(domain.getStockOutType() != null ? domain.getStockOutType().code() : null);
        entity.setDestinationWarehouseId(domain.getDestinationWarehouseId());
        entity.setCustomerId(domain.getCustomerId());
        entity.setDisposalReason(domain.getDisposalReason());
        return entity;
    }

    public StockOutTransaction toDomainModel(StockOutTransactionJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new StockOutTransaction()
                .setId(entity.getId())
                .setTransactionCode(entity.getTransactionCode())
                .setWarehouseId(entity.getWarehouseId())
                .setDestinationBranchId(entity.getDestinationBranchId())
                .setTotalAmount(entity.getTotalAmount())
                .setTransactionDate(entity.getTransactionDate())
                .setReferenceNumber(entity.getReferenceNumber())
                .setNotes(entity.getNotes())
                .setPerformedBy(entity.getPerformedBy())
                .setIssuedBy(entity.getIssuedBy())
                .setReceivedBy(entity.getReceivedBy())
                .setStatus(entity.getStatus())
                .setIsLocked(entity.getIsLocked())
                .setCreatedBy(entity.getCreatedBy())
                .setUpdatedBy(entity.getUpdatedBy())
                .setCreatedDate(entity.getCreatedDate())
                .setUpdatedDate(entity.getUpdatedDate())
                // Stock Out Type fields
                .setStockOutType(entity.getStockOutType() != null ? 
                    com.restaurant.ddd.domain.enums.StockOutType.fromCode(entity.getStockOutType()) : null)
                .setDestinationWarehouseId(entity.getDestinationWarehouseId())
                .setCustomerId(entity.getCustomerId())
                .setDisposalReason(entity.getDisposalReason());
    }
}
