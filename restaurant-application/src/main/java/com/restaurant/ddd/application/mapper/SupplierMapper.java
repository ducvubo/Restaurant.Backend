package com.restaurant.ddd.application.mapper;

import com.restaurant.ddd.application.model.supplier.SupplierDTO;
import com.restaurant.ddd.domain.model.Supplier;

/**
 * Mapper between Supplier domain model and SupplierDTO
 */
public class SupplierMapper {

    public static SupplierDTO toDTO(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        SupplierDTO dto = new SupplierDTO();
        dto.setId(supplier.getId());
        dto.setCode(supplier.getCode());
        dto.setName(supplier.getName());
        dto.setContactPerson(supplier.getContactPerson());
        dto.setEmail(supplier.getEmail());
        dto.setPhone(supplier.getPhone());
        dto.setAddress(supplier.getAddress());
        dto.setTaxCode(supplier.getTaxCode());
        dto.setPaymentTerms(supplier.getPaymentTerms());
        dto.setRating(supplier.getRating());
        dto.setNotes(supplier.getNotes());
        dto.setStatus(supplier.getStatus() != null ? supplier.getStatus().code() : null);
        dto.setCreatedBy(supplier.getCreatedBy());
        dto.setUpdatedBy(supplier.getUpdatedBy());
        dto.setCreatedDate(supplier.getCreatedDate());
        dto.setUpdatedDate(supplier.getUpdatedDate());

        return dto;
    }
}
