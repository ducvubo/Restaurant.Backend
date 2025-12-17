package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.supplier.*;

import java.util.List;
import java.util.UUID;

/**
 * Application Service interface for Supplier
 */
public interface SupplierAppService {
    SupplierDTO createSupplier(CreateSupplierRequest request);
    SupplierDTO getSupplierById(UUID id);
    List<SupplierDTO> getAllSuppliers();
    SupplierListResponse getList(SupplierListRequest request);
    SupplierDTO updateSupplier(UUID id, UpdateSupplierRequest request);
    SupplierDTO activateSupplier(UUID id);
    SupplierDTO deactivateSupplier(UUID id);
}
