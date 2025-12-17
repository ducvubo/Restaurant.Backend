package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.StockOutItem;
import com.restaurant.ddd.infrastructure.persistence.entity.StockOutItemJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StockOutItemDataAccessMapper {

    public StockOutItem toDomain(StockOutItemJpaEntity entity) {
        if (entity == null) return null;
        return new StockOutItem()
                .setId(entity.getId())
                .setStockOutTransactionId(entity.getStockOutTransactionId())
                .setMaterialId(entity.getMaterialId())
                .setUnitId(entity.getUnitId())
                .setQuantity(entity.getQuantity())
                .setUnitPrice(entity.getUnitPrice())
                .setTotalAmount(entity.getTotalAmount())
                .setNotes(entity.getNotes())
                .setCreatedDate(entity.getCreatedDate());
    }

    public StockOutItemJpaEntity toEntity(StockOutItem domain) {
        if (domain == null) return null;
        StockOutItemJpaEntity entity = new StockOutItemJpaEntity();
        if (domain.getId() != null) entity.setId(domain.getId());
        entity.setStockOutTransactionId(domain.getStockOutTransactionId());
        entity.setMaterialId(domain.getMaterialId());
        entity.setUnitId(domain.getUnitId());
        entity.setQuantity(domain.getQuantity());
        entity.setUnitPrice(domain.getUnitPrice());
        entity.setTotalAmount(domain.getTotalAmount());
        entity.setNotes(domain.getNotes());
        entity.setCreatedDate(domain.getCreatedDate());
        return entity;
    }

    public List<StockOutItem> toDomainList(List<StockOutItemJpaEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
