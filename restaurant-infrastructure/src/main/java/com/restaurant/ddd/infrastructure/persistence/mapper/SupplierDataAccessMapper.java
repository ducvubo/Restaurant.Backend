package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.Supplier;
import com.restaurant.ddd.infrastructure.persistence.entity.SupplierJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between Supplier domain model and SupplierJpaEntity
 */
@Component
public class SupplierDataAccessMapper {

    /**
     * Convert JPA entity to domain model
     */
    public Supplier toDomain(SupplierJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Supplier supplier = new Supplier();
        supplier.setId(entity.getId());
        supplier.setCode(entity.getCode());
        supplier.setName(entity.getName());
        supplier.setContactPerson(entity.getContactPerson());
        supplier.setEmail(entity.getEmail());
        supplier.setPhone(entity.getPhone());
        supplier.setAddress(entity.getAddress());
        supplier.setTaxCode(entity.getTaxCode());
        supplier.setPaymentTerms(entity.getPaymentTerms());
        supplier.setRating(entity.getRating());
        supplier.setNotes(entity.getNotes());
        supplier.setStatus(entity.getStatus());
        supplier.setCreatedBy(entity.getCreatedBy());
        supplier.setUpdatedBy(entity.getUpdatedBy());
        supplier.setCreatedDate(entity.getCreatedDate());
        supplier.setUpdatedDate(entity.getUpdatedDate());

        return supplier;
    }

    /**
     * Convert domain model to JPA entity
     */
    public SupplierJpaEntity toEntity(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        SupplierJpaEntity entity = new SupplierJpaEntity();
        entity.setId(supplier.getId());
        entity.setCode(supplier.getCode());
        entity.setName(supplier.getName());
        entity.setContactPerson(supplier.getContactPerson());
        entity.setEmail(supplier.getEmail());
        entity.setPhone(supplier.getPhone());
        entity.setAddress(supplier.getAddress());
        entity.setTaxCode(supplier.getTaxCode());
        entity.setPaymentTerms(supplier.getPaymentTerms());
        entity.setRating(supplier.getRating());
        entity.setNotes(supplier.getNotes());
        entity.setStatus(supplier.getStatus());
        entity.setCreatedBy(supplier.getCreatedBy());
        entity.setUpdatedBy(supplier.getUpdatedBy());
        entity.setCreatedDate(supplier.getCreatedDate());
        entity.setUpdatedDate(supplier.getUpdatedDate());

        return entity;
    }
}
