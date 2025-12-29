package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.enums.RfqStatus;
import com.restaurant.ddd.domain.model.RequestForQuotation;
import com.restaurant.ddd.domain.model.RfqItem;
import com.restaurant.ddd.infrastructure.persistence.entity.RfqItemJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.entity.RfqJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between RequestForQuotation domain model and JPA entities
 */
@Component
public class RfqDataAccessMapper {

    /**
     * Convert JPA entity to domain model
     */
    public RequestForQuotation toDomain(RfqJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        RequestForQuotation rfq = new RequestForQuotation();
        rfq.setId(entity.getId());
        rfq.setRfqCode(entity.getRfqCode());
        rfq.setRequisitionId(entity.getRequisitionId());
        rfq.setSupplierId(entity.getSupplierId());
        rfq.setSentDate(entity.getSentDate());
        rfq.setValidUntil(entity.getValidUntil());
        rfq.setTotalAmount(entity.getTotalAmount());
        rfq.setPaymentTerms(entity.getPaymentTerms());
        rfq.setDeliveryTerms(entity.getDeliveryTerms());
        rfq.setNotes(entity.getNotes());
        rfq.setStatus(RfqStatus.fromCode(entity.getStatus() != null ? entity.getStatus().code() : null));
        rfq.setCreatedBy(entity.getCreatedBy());
        rfq.setUpdatedBy(entity.getUpdatedBy());
        rfq.setCreatedDate(entity.getCreatedDate());
        rfq.setUpdatedDate(entity.getUpdatedDate());

        return rfq;
    }

    /**
     * Convert domain model to JPA entity
     */
    public RfqJpaEntity toEntity(RequestForQuotation rfq) {
        if (rfq == null) {
            return null;
        }

        RfqJpaEntity entity = new RfqJpaEntity();
        entity.setId(rfq.getId());
        entity.setRfqCode(rfq.getRfqCode());
        entity.setRequisitionId(rfq.getRequisitionId());
        entity.setSupplierId(rfq.getSupplierId());
        entity.setSentDate(rfq.getSentDate());
        entity.setValidUntil(rfq.getValidUntil());
        entity.setTotalAmount(rfq.getTotalAmount());
        entity.setPaymentTerms(rfq.getPaymentTerms());
        entity.setDeliveryTerms(rfq.getDeliveryTerms());
        entity.setNotes(rfq.getNotes());
        entity.setCreatedBy(rfq.getCreatedBy());
        entity.setUpdatedBy(rfq.getUpdatedBy());
        entity.setCreatedDate(rfq.getCreatedDate());
        entity.setUpdatedDate(rfq.getUpdatedDate());
        
        // Set status using DataStatus from BaseJpaEntity
        if (rfq.getStatus() != null) {
            entity.setStatus(com.restaurant.ddd.domain.enums.DataStatus.fromCode(rfq.getStatus().code()));
        }

        return entity;
    }

    /**
     * Convert item entity to domain
     */
    public RfqItem itemToDomain(RfqItemJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        RfqItem item = new RfqItem();
        item.setId(entity.getId());
        item.setRfqId(entity.getRfqId());
        item.setMaterialId(entity.getMaterialId());
        item.setQuantity(entity.getQuantity());
        item.setUnitId(entity.getUnitId());
        item.setUnitPrice(entity.getUnitPrice());
        item.setAmount(entity.getAmount());
        item.setNotes(entity.getNotes());

        return item;
    }

    /**
     * Convert item domain to entity
     */
    public RfqItemJpaEntity itemToEntity(RfqItem item) {
        if (item == null) {
            return null;
        }

        RfqItemJpaEntity entity = new RfqItemJpaEntity();
        entity.setId(item.getId());
        entity.setRfqId(item.getRfqId());
        entity.setMaterialId(item.getMaterialId());
        entity.setQuantity(item.getQuantity());
        entity.setUnitId(item.getUnitId());
        entity.setUnitPrice(item.getUnitPrice());
        entity.setAmount(item.getAmount());
        entity.setNotes(item.getNotes());

        return entity;
    }

    /**
     * Convert list of item entities to domain
     */
    public List<RfqItem> itemsToDomain(List<RfqItemJpaEntity> entities) {
        return entities.stream()
                .map(this::itemToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of item domains to entities
     */
    public List<RfqItemJpaEntity> itemsToEntity(List<RfqItem> items) {
        return items.stream()
                .map(this::itemToEntity)
                .collect(Collectors.toList());
    }
}
