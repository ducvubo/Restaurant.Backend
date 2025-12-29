package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.enums.PurchaseOrderStatus;
import com.restaurant.ddd.domain.model.PurchaseOrder;
import com.restaurant.ddd.domain.model.PurchaseOrderItem;
import com.restaurant.ddd.infrastructure.persistence.entity.PurchaseOrderItemJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.entity.PurchaseOrderJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between PurchaseOrder domain model and JPA entities
 */
@Component
public class PurchaseOrderDataAccessMapper {

    /**
     * Convert JPA entity to domain model
     */
    public PurchaseOrder toDomain(PurchaseOrderJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        PurchaseOrder po = new PurchaseOrder();
        po.setId(entity.getId());
        po.setPoCode(entity.getPoCode());
        po.setRfqId(entity.getRfqId());
        po.setSupplierId(entity.getSupplierId());
        po.setWarehouseId(entity.getWarehouseId());
        po.setOrderDate(entity.getOrderDate());
        po.setExpectedDeliveryDate(entity.getExpectedDeliveryDate());
        po.setPaymentTerms(entity.getPaymentTerms());
        po.setDeliveryTerms(entity.getDeliveryTerms());
        po.setTotalAmount(entity.getTotalAmount());
        po.setReceivedAmount(entity.getReceivedAmount());
        po.setNotes(entity.getNotes());
        po.setStatus(PurchaseOrderStatus.fromCode(entity.getStatus() != null ? entity.getStatus().code() : null));
        po.setCreatedBy(entity.getCreatedBy());
        po.setUpdatedBy(entity.getUpdatedBy());
        po.setCreatedDate(entity.getCreatedDate());
        po.setUpdatedDate(entity.getUpdatedDate());

        return po;
    }

    /**
     * Convert domain model to JPA entity
     */
    public PurchaseOrderJpaEntity toEntity(PurchaseOrder po) {
        if (po == null) {
            return null;
        }

        PurchaseOrderJpaEntity entity = new PurchaseOrderJpaEntity();
        entity.setId(po.getId());
        entity.setPoCode(po.getPoCode());
        entity.setRfqId(po.getRfqId());
        entity.setSupplierId(po.getSupplierId());
        entity.setWarehouseId(po.getWarehouseId());
        entity.setOrderDate(po.getOrderDate());
        entity.setExpectedDeliveryDate(po.getExpectedDeliveryDate());
        entity.setPaymentTerms(po.getPaymentTerms());
        entity.setDeliveryTerms(po.getDeliveryTerms());
        entity.setTotalAmount(po.getTotalAmount());
        entity.setReceivedAmount(po.getReceivedAmount());
        entity.setNotes(po.getNotes());
        entity.setCreatedBy(po.getCreatedBy());
        entity.setUpdatedBy(po.getUpdatedBy());
        entity.setCreatedDate(po.getCreatedDate());
        entity.setUpdatedDate(po.getUpdatedDate());
        
        // Set status using DataStatus from BaseJpaEntity
        if (po.getStatus() != null) {
            entity.setStatus(com.restaurant.ddd.domain.enums.DataStatus.fromCode(po.getStatus().code()));
        }

        return entity;
    }

    /**
     * Convert item entity to domain
     */
    public PurchaseOrderItem itemToDomain(PurchaseOrderItemJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setId(entity.getId());
        item.setPoId(entity.getPoId());
        item.setMaterialId(entity.getMaterialId());
        item.setQuantity(entity.getQuantity());
        item.setReceivedQuantity(entity.getReceivedQuantity());
        item.setUnitId(entity.getUnitId());
        item.setUnitPrice(entity.getUnitPrice());
        item.setAmount(entity.getAmount());
        item.setNotes(entity.getNotes());

        return item;
    }

    /**
     * Convert item domain to entity
     */
    public PurchaseOrderItemJpaEntity itemToEntity(PurchaseOrderItem item) {
        if (item == null) {
            return null;
        }

        PurchaseOrderItemJpaEntity entity = new PurchaseOrderItemJpaEntity();
        entity.setId(item.getId());
        entity.setPoId(item.getPoId());
        entity.setMaterialId(item.getMaterialId());
        entity.setQuantity(item.getQuantity());
        entity.setReceivedQuantity(item.getReceivedQuantity());
        entity.setUnitId(item.getUnitId());
        entity.setUnitPrice(item.getUnitPrice());
        entity.setAmount(item.getAmount());
        entity.setNotes(item.getNotes());

        return entity;
    }

    /**
     * Convert list of item entities to domain
     */
    public List<PurchaseOrderItem> itemsToDomain(List<PurchaseOrderItemJpaEntity> entities) {
        return entities.stream()
                .map(this::itemToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of item domains to entities
     */
    public List<PurchaseOrderItemJpaEntity> itemsToEntity(List<PurchaseOrderItem> items) {
        return items.stream()
                .map(this::itemToEntity)
                .collect(Collectors.toList());
    }
}
