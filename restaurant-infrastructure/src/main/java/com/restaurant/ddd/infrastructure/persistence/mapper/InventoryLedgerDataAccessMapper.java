package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.InventoryLedger;
import com.restaurant.ddd.infrastructure.persistence.entity.InventoryLedgerJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryLedgerDataAccessMapper {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InventoryLedgerDataAccessMapper.class);

    public InventoryLedger inventoryLedgerJpaEntityToInventoryLedger(InventoryLedgerJpaEntity entity) {
        if (entity == null) return null;
        return new InventoryLedger()
                .setId(entity.getId())
                .setWarehouseId(entity.getWarehouseId())
                .setMaterialId(entity.getMaterialId())
                .setTransactionId(entity.getTransactionId())
                .setTransactionCode(entity.getTransactionCode())
                .setTransactionDate(entity.getTransactionDate())
                .setInventoryMethod(entity.getInventoryMethod())
                .setQuantity(entity.getQuantity())
                .setUnitId(entity.getUnitId())
                .setUnitPrice(entity.getUnitPrice())
                .setRemainingQuantity(entity.getRemainingQuantity())
                .setStatus(entity.getStatus())
                .setBatchNumber(entity.getBatchNumber())
                .setCreatedDate(entity.getCreatedDate())
                // Unit conversion fields
                .setOriginalUnitId(entity.getOriginalUnitId())
                .setOriginalQuantity(entity.getOriginalQuantity())
                .setBaseUnitId(entity.getBaseUnitId())
                .setConversionFactor(entity.getConversionFactor());
    }

    public InventoryLedgerJpaEntity inventoryLedgerToInventoryLedgerJpaEntity(InventoryLedger domain) {
        if (domain == null) return null;
        
        log.info("[MAPPER] Input domain - originalUnitId: {}, baseUnitId: {}, convFactor: {}, originalQty: {}",
            domain.getOriginalUnitId(), domain.getBaseUnitId(), domain.getConversionFactor(), domain.getOriginalQuantity());
        
        InventoryLedgerJpaEntity entity = new InventoryLedgerJpaEntity();
        entity.setId(domain.getId());
        entity.setWarehouseId(domain.getWarehouseId());
        entity.setMaterialId(domain.getMaterialId());
        entity.setTransactionId(domain.getTransactionId());
        entity.setTransactionCode(domain.getTransactionCode());
        entity.setTransactionDate(domain.getTransactionDate());
        entity.setInventoryMethod(domain.getInventoryMethod());
        entity.setQuantity(domain.getQuantity());
        entity.setUnitId(domain.getUnitId());
        entity.setUnitPrice(domain.getUnitPrice());
        entity.setRemainingQuantity(domain.getRemainingQuantity());
        entity.setStatus(domain.getStatus());
        entity.setBatchNumber(domain.getBatchNumber());
        entity.setCreatedDate(domain.getCreatedDate());
        // Unit conversion fields
        entity.setOriginalUnitId(domain.getOriginalUnitId());
        entity.setOriginalQuantity(domain.getOriginalQuantity());
        entity.setBaseUnitId(domain.getBaseUnitId());
        entity.setConversionFactor(domain.getConversionFactor());
        
        log.info("[MAPPER] Output entity - originalUnitId: {}, baseUnitId: {}, convFactor: {}, originalQty: {}",
            entity.getOriginalUnitId(), entity.getBaseUnitId(), entity.getConversionFactor(), entity.getOriginalQuantity());
        
        return entity;
    }

    public List<InventoryLedger> inventoryLedgerJpaEntitiesToInventoryLedgers(List<InventoryLedgerJpaEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::inventoryLedgerJpaEntityToInventoryLedger).collect(Collectors.toList());
    }
}
