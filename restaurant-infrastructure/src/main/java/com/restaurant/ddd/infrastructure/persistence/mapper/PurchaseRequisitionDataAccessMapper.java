package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.enums.PurchasePriority;
import com.restaurant.ddd.domain.enums.PurchaseRequisitionStatus;
import com.restaurant.ddd.domain.model.PurchaseRequisition;
import com.restaurant.ddd.domain.model.PurchaseRequisitionItem;
import com.restaurant.ddd.infrastructure.persistence.entity.PurchaseRequisitionItemJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.entity.PurchaseRequisitionJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between PurchaseRequisition domain model and JPA entities
 */
@Component
public class PurchaseRequisitionDataAccessMapper {

    /**
     * Convert JPA entity to domain model
     */
    public PurchaseRequisition toDomain(PurchaseRequisitionJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        PurchaseRequisition pr = new PurchaseRequisition();
        pr.setId(entity.getId());
        pr.setRequisitionCode(entity.getRequisitionCode());
        pr.setWarehouseId(entity.getWarehouseId());
        pr.setRequestedBy(entity.getRequestedBy());
        pr.setRequestDate(entity.getRequestDate());
        pr.setRequiredDate(entity.getRequiredDate());
        pr.setPriority(PurchasePriority.fromCode(entity.getPriority()));
        pr.setNotes(entity.getNotes());
        // Dùng requisitionStatus thay vì status của BaseJpaEntity
        pr.setStatus(PurchaseRequisitionStatus.fromCode(entity.getRequisitionStatus()));
        pr.setApprovedBy(entity.getApprovedBy());
        pr.setApprovedDate(entity.getApprovedDate());
        pr.setRejectionReason(entity.getRejectionReason());
        pr.setWorkflowId(entity.getWorkflowId());
        pr.setWorkflowStep(entity.getWorkflowStep());
        pr.setCreatedBy(entity.getCreatedBy());
        pr.setUpdatedBy(entity.getUpdatedBy());
        pr.setCreatedDate(entity.getCreatedDate());
        pr.setUpdatedDate(entity.getUpdatedDate());

        return pr;
    }

    /**
     * Convert domain model to JPA entity
     */
    public PurchaseRequisitionJpaEntity toEntity(PurchaseRequisition pr) {
        if (pr == null) {
            return null;
        }

        PurchaseRequisitionJpaEntity entity = new PurchaseRequisitionJpaEntity();
        entity.setId(pr.getId());
        entity.setRequisitionCode(pr.getRequisitionCode());
        entity.setWarehouseId(pr.getWarehouseId());
        entity.setRequestedBy(pr.getRequestedBy());
        entity.setRequestDate(pr.getRequestDate());
        entity.setRequiredDate(pr.getRequiredDate());
        entity.setPriority(pr.getPriority() != null ? pr.getPriority().code() : null);
        entity.setNotes(pr.getNotes());
        entity.setApprovedBy(pr.getApprovedBy());
        entity.setApprovedDate(pr.getApprovedDate());
        entity.setRejectionReason(pr.getRejectionReason());
        entity.setWorkflowId(pr.getWorkflowId());
        entity.setWorkflowStep(pr.getWorkflowStep());
        entity.setCreatedBy(pr.getCreatedBy());
        entity.setUpdatedBy(pr.getUpdatedBy());
        entity.setCreatedDate(pr.getCreatedDate());
        entity.setUpdatedDate(pr.getUpdatedDate());
        
        // Lưu requisition status riêng (không phải DataStatus của BaseJpaEntity)
        if (pr.getStatus() != null) {
            entity.setRequisitionStatus(pr.getStatus().code());
        }

        return entity;
    }

    /**
     * Convert item entity to domain
     */
    public PurchaseRequisitionItem itemToDomain(PurchaseRequisitionItemJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        PurchaseRequisitionItem item = new PurchaseRequisitionItem();
        item.setId(entity.getId());
        item.setRequisitionId(entity.getRequisitionId());
        item.setMaterialId(entity.getMaterialId());
        item.setQuantity(entity.getQuantity());
        item.setUnitId(entity.getUnitId());
        item.setEstimatedPrice(entity.getEstimatedPrice());
        item.setEstimatedAmount(entity.getEstimatedAmount());
        item.setNotes(entity.getNotes());

        return item;
    }

    /**
     * Convert item domain to entity
     */
    public PurchaseRequisitionItemJpaEntity itemToEntity(PurchaseRequisitionItem item) {
        if (item == null) {
            return null;
        }

        PurchaseRequisitionItemJpaEntity entity = new PurchaseRequisitionItemJpaEntity();
        entity.setId(item.getId());
        entity.setRequisitionId(item.getRequisitionId());
        entity.setMaterialId(item.getMaterialId());
        entity.setQuantity(item.getQuantity());
        entity.setUnitId(item.getUnitId());
        entity.setEstimatedPrice(item.getEstimatedPrice());
        entity.setEstimatedAmount(item.getEstimatedAmount());
        entity.setNotes(item.getNotes());

        return entity;
    }

    /**
     * Convert list of item entities to domain
     */
    public List<PurchaseRequisitionItem> itemsToDomain(List<PurchaseRequisitionItemJpaEntity> entities) {
        return entities.stream()
                .map(this::itemToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of item domains to entities
     */
    public List<PurchaseRequisitionItemJpaEntity> itemsToEntity(List<PurchaseRequisitionItem> items) {
        return items.stream()
                .map(this::itemToEntity)
                .collect(Collectors.toList());
    }
}
