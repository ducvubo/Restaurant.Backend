package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.StockInItem;
import com.restaurant.ddd.infrastructure.persistence.entity.StockInItemJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StockInItemDataAccessMapper {

    public StockInItem toDomain(StockInItemJpaEntity entity) {
        if (entity == null) return null;
        return new StockInItem()
                .setId(entity.getId())
                .setStockInTransactionId(entity.getStockInTransactionId())
                .setMaterialId(entity.getMaterialId())
                .setUnitId(entity.getUnitId())
                .setQuantity(entity.getQuantity())
                .setUnitPrice(entity.getUnitPrice())
                .setTotalAmount(entity.getTotalAmount())
                .setNotes(entity.getNotes())
                .setCreatedDate(entity.getCreatedDate());
    }

    public StockInItemJpaEntity toEntity(StockInItem domain) {
        if (domain == null) return null;
        StockInItemJpaEntity entity = new StockInItemJpaEntity();
        if (domain.getId() != null) entity.setId(domain.getId());
        entity.setStockInTransactionId(domain.getStockInTransactionId());
        entity.setMaterialId(domain.getMaterialId());
        entity.setUnitId(domain.getUnitId());
        entity.setQuantity(domain.getQuantity());
        entity.setUnitPrice(domain.getUnitPrice());
        entity.setTotalAmount(domain.getTotalAmount());
        entity.setNotes(domain.getNotes());
        entity.setCreatedDate(domain.getCreatedDate());
        return entity;
    }

    public List<StockInItem> toDomainList(List<StockInItemJpaEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
