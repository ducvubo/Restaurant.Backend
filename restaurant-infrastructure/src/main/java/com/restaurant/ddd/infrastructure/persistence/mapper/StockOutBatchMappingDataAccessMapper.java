package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.StockOutBatchMapping;
import com.restaurant.ddd.infrastructure.persistence.entity.StockOutBatchMappingJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StockOutBatchMappingDataAccessMapper {

    public StockOutBatchMapping toDomain(StockOutBatchMappingJpaEntity entity) {
        if (entity == null) return null;
        
        return new StockOutBatchMapping()
                .setId(entity.getId())
                .setStockOutItemId(entity.getStockOutItemId())
                .setInventoryLedgerId(entity.getInventoryLedgerId())
                .setQuantityUsed(entity.getQuantityUsed())
                .setUnitPrice(entity.getUnitPrice())
                .setCreatedDate(entity.getCreatedDate());
    }

    public StockOutBatchMappingJpaEntity toEntity(StockOutBatchMapping domain) {
        if (domain == null) return null;
        
        StockOutBatchMappingJpaEntity entity = new StockOutBatchMappingJpaEntity();
        entity.setId(domain.getId());
        entity.setStockOutItemId(domain.getStockOutItemId());
        entity.setInventoryLedgerId(domain.getInventoryLedgerId());
        entity.setQuantityUsed(domain.getQuantityUsed());
        entity.setUnitPrice(domain.getUnitPrice());
        entity.setCreatedDate(domain.getCreatedDate());
        return entity;
    }

    public List<StockOutBatchMapping> toDomainList(List<StockOutBatchMappingJpaEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
