package com.restaurant.ddd.application.mapper;

import com.restaurant.ddd.application.model.purchasing.*;
import com.restaurant.ddd.domain.enums.PurchaseOrderStatus;
import com.restaurant.ddd.domain.model.PurchaseOrder;
import com.restaurant.ddd.domain.model.PurchaseOrderItem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for PurchaseOrder
 */
public class PurchaseOrderMapper {

    public static PurchaseOrderDTO toDTO(PurchaseOrder domain) {
        if (domain == null) return null;
        
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setId(domain.getId());
        dto.setPoCode(domain.getPoCode());
        dto.setRfqId(domain.getRfqId());
        dto.setRfqCode(domain.getRfqCode());
        dto.setSupplierId(domain.getSupplierId());
        dto.setSupplierName(domain.getSupplierName());
        dto.setWarehouseId(domain.getWarehouseId());
        dto.setWarehouseName(domain.getWarehouseName());
        dto.setOrderDate(domain.getOrderDate());
        dto.setExpectedDeliveryDate(domain.getExpectedDeliveryDate());
        dto.setPaymentTerms(domain.getPaymentTerms());
        dto.setDeliveryTerms(domain.getDeliveryTerms());
        dto.setTotalAmount(domain.getTotalAmount());
        dto.setReceivedAmount(domain.getReceivedAmount());
        dto.setRemainingAmount(domain.getRemainingAmount());
        dto.setReceivingProgress(domain.getReceivingProgress());
        dto.setNotes(domain.getNotes());
        dto.setStatus(domain.getStatus() != null ? domain.getStatus().code() : null);
        dto.setStatusName(domain.getStatus() != null ? domain.getStatus().message() : null);
        dto.setCreatedBy(domain.getCreatedBy());
        dto.setUpdatedBy(domain.getUpdatedBy());
        dto.setCreatedDate(domain.getCreatedDate());
        dto.setUpdatedDate(domain.getUpdatedDate());
        
        if (domain.getItems() != null) {
            dto.setItems(domain.getItems().stream()
                    .map(PurchaseOrderMapper::toItemDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }

    public static PurchaseOrderItemDTO toItemDTO(PurchaseOrderItem item) {
        if (item == null) return null;
        
        PurchaseOrderItemDTO dto = new PurchaseOrderItemDTO();
        dto.setId(item.getId());
        dto.setPoId(item.getPoId());
        dto.setMaterialId(item.getMaterialId());
        dto.setMaterialCode(item.getMaterialCode());
        dto.setMaterialName(item.getMaterialName());
        dto.setQuantity(item.getQuantity());
        dto.setReceivedQuantity(item.getReceivedQuantity());
        dto.setRemainingQuantity(item.getRemainingQuantity());
        dto.setUnitId(item.getUnitId());
        dto.setUnitName(item.getUnitName());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setAmount(item.getAmount());
        dto.setNotes(item.getNotes());
        dto.setIsFullyReceived(item.isFullyReceived());
        
        return dto;
    }

    public static PurchaseOrderItem toItemDomain(PurchaseOrderItemRequest request) {
        if (request == null) return null;
        
        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setId(request.getId());
        item.setMaterialId(request.getMaterialId());
        item.setQuantity(request.getQuantity());
        item.setUnitId(request.getUnitId());
        item.setUnitPrice(request.getUnitPrice());
        item.setNotes(request.getNotes());
        item.calculateAmount();
        
        return item;
    }

    public static List<PurchaseOrderItem> toItemDomainList(List<PurchaseOrderItemRequest> requests) {
        if (requests == null) return null;
        return requests.stream()
                .map(PurchaseOrderMapper::toItemDomain)
                .collect(Collectors.toList());
    }
}
