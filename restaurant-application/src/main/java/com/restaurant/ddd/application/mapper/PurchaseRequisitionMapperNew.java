



package com.restaurant.ddd.application.mapper;

import com.restaurant.ddd.application.model.purchasing.*;
        import com.restaurant.ddd.domain.enums.PurchasePriority;
import com.restaurant.ddd.domain.enums.PurchaseRequisitionStatus;
import com.restaurant.ddd.domain.model.PurchaseRequisition;
import com.restaurant.ddd.domain.model.PurchaseRequisitionItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for PurchaseRequisition
 */
public class PurchaseRequisitionMapperNew {

    public static PurchaseRequisitionDTO toDTO(PurchaseRequisition domain) {
        if (domain == null) return null;

        PurchaseRequisitionDTO dto = new PurchaseRequisitionDTO();
        dto.setId(domain.getId());
        dto.setRequisitionCode(domain.getRequisitionCode());
        dto.setWarehouseId(domain.getWarehouseId());
        dto.setWarehouseName(domain.getWarehouseName());
        dto.setRequestedBy(domain.getRequestedBy());
        dto.setRequestedByName(domain.getRequestedByName());
        dto.setRequestDate(domain.getRequestDate());
        dto.setRequiredDate(domain.getRequiredDate());
        dto.setPriority(domain.getPriority() != null ? domain.getPriority().code() : null);
        dto.setPriorityName(domain.getPriority() != null ? domain.getPriority().message() : null);
        dto.setNotes(domain.getNotes());
        dto.setStatus(domain.getStatus() != null ? domain.getStatus().code() : null);
        dto.setStatusName(domain.getStatus() != null ? domain.getStatus().message() : null);
        dto.setApprovedBy(domain.getApprovedBy());
        dto.setApprovedByName(domain.getApprovedByName());
        dto.setApprovedDate(domain.getApprovedDate());
        dto.setRejectionReason(domain.getRejectionReason());
        dto.setWorkflowId(domain.getWorkflowId());
        dto.setWorkflowStep(domain.getWorkflowStep());
        dto.setCreatedBy(domain.getCreatedBy());
        dto.setUpdatedBy(domain.getUpdatedBy());
        dto.setCreatedDate(domain.getCreatedDate());
        dto.setUpdatedDate(domain.getUpdatedDate());

        if (domain.getItems() != null) {
            dto.setItems(domain.getItems().stream()
                    .map(PurchaseRequisitionMapperNew::toItemDTO)
                    .collect(Collectors.toList()));

            // Calculate total estimated amount
            dto.setTotalEstimatedAmount(domain.getItems().stream()
                    .map(PurchaseRequisitionItem::getEstimatedAmount)
                    .filter(a -> a != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        return dto;
    }

    public static PurchaseRequisitionItemDTO toItemDTO(PurchaseRequisitionItem item) {
        if (item == null) return null;

        PurchaseRequisitionItemDTO dto = new PurchaseRequisitionItemDTO();
        dto.setId(item.getId());
        dto.setRequisitionId(item.getRequisitionId());
        dto.setMaterialId(item.getMaterialId());
        dto.setMaterialCode(item.getMaterialCode());
        dto.setMaterialName(item.getMaterialName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitId(item.getUnitId());
        dto.setUnitName(item.getUnitName());
        dto.setEstimatedPrice(item.getEstimatedPrice());
        dto.setEstimatedAmount(item.getEstimatedAmount());
        dto.setNotes(item.getNotes());

        return dto;
    }

    public static PurchaseRequisitionItem toItemDomain(PurchaseRequisitionItemRequest request) {
        if (request == null) return null;

        PurchaseRequisitionItem item = new PurchaseRequisitionItem();
        item.setId(request.getId());
        item.setMaterialId(request.getMaterialId());
        item.setQuantity(request.getQuantity());
        item.setUnitId(request.getUnitId());
        item.setEstimatedPrice(request.getEstimatedPrice());
        item.setNotes(request.getNotes());
        item.calculateEstimatedAmount();

        return item;
    }

    public static List<PurchaseRequisitionItem> toItemDomainList(List<PurchaseRequisitionItemRequest> requests) {
        if (requests == null) return null;
        return requests.stream()
                .map(PurchaseRequisitionMapperNew::toItemDomain)
                .collect(Collectors.toList());
    }
}

