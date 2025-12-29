package com.restaurant.ddd.application.mapper;

import com.restaurant.ddd.application.model.purchasing.*;
import com.restaurant.ddd.domain.enums.RfqStatus;
import com.restaurant.ddd.domain.model.RequestForQuotation;
import com.restaurant.ddd.domain.model.RfqItem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for RFQ
 */
public class RfqMapper {

    public static RfqDTO toDTO(RequestForQuotation domain) {
        if (domain == null) return null;
        
        RfqDTO dto = new RfqDTO();
        dto.setId(domain.getId());
        dto.setRfqCode(domain.getRfqCode());
        dto.setRequisitionId(domain.getRequisitionId());
        dto.setRequisitionCode(domain.getRequisitionCode());
        dto.setSupplierId(domain.getSupplierId());
        dto.setSupplierName(domain.getSupplierName());
        dto.setSentDate(domain.getSentDate());
        dto.setValidUntil(domain.getValidUntil());
        dto.setTotalAmount(domain.getTotalAmount());
        dto.setPaymentTerms(domain.getPaymentTerms());
        dto.setDeliveryTerms(domain.getDeliveryTerms());
        dto.setNotes(domain.getNotes());
        dto.setStatus(domain.getStatus() != null ? domain.getStatus().code() : null);
        dto.setStatusName(domain.getStatus() != null ? domain.getStatus().message() : null);
        dto.setCreatedBy(domain.getCreatedBy());
        dto.setUpdatedBy(domain.getUpdatedBy());
        dto.setCreatedDate(domain.getCreatedDate());
        dto.setUpdatedDate(domain.getUpdatedDate());
        
        if (domain.getItems() != null) {
            dto.setItems(domain.getItems().stream()
                    .map(RfqMapper::toItemDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }

    public static RfqItemDTO toItemDTO(RfqItem item) {
        if (item == null) return null;
        
        RfqItemDTO dto = new RfqItemDTO();
        dto.setId(item.getId());
        dto.setRfqId(item.getRfqId());
        dto.setMaterialId(item.getMaterialId());
        dto.setMaterialCode(item.getMaterialCode());
        dto.setMaterialName(item.getMaterialName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitId(item.getUnitId());
        dto.setUnitName(item.getUnitName());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setAmount(item.getAmount());
        dto.setNotes(item.getNotes());
        
        return dto;
    }

    public static RfqItem toItemDomain(RfqItemRequest request) {
        if (request == null) return null;
        
        RfqItem item = new RfqItem();
        item.setId(request.getId());
        item.setMaterialId(request.getMaterialId());
        item.setQuantity(request.getQuantity());
        item.setUnitId(request.getUnitId());
        item.setUnitPrice(request.getUnitPrice());
        item.setNotes(request.getNotes());
        item.calculateAmount();
        
        return item;
    }

    public static List<RfqItem> toItemDomainList(List<RfqItemRequest> requests) {
        if (requests == null) return null;
        return requests.stream()
                .map(RfqMapper::toItemDomain)
                .collect(Collectors.toList());
    }
}
