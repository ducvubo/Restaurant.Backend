package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.SupplierMapper;
import com.restaurant.ddd.application.model.supplier.*;
import com.restaurant.ddd.application.service.SupplierAppService;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Supplier;
import com.restaurant.ddd.domain.respository.SupplierRepository;
import com.restaurant.ddd.infrastructure.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of SupplierAppService
 */
@Service
@Slf4j
public class SupplierAppServiceImpl implements SupplierAppService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    @Transactional
    public SupplierDTO createSupplier(CreateSupplierRequest request) {
        // Validate code uniqueness
        if (supplierRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã nhà cung cấp đã tồn tại: " + request.getCode());
        }

        // Validate email uniqueness
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() 
                && supplierRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại: " + request.getEmail());
        }

        // Create domain model
        Supplier supplier = new Supplier();
        supplier.setCode(request.getCode());
        supplier.setName(request.getName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setTaxCode(request.getTaxCode());
        supplier.setPaymentTerms(request.getPaymentTerms());
        supplier.setRating(request.getRating());
        supplier.setNotes(request.getNotes());
        supplier.setStatus(DataStatus.ACTIVE);
        supplier.setCreatedBy(SecurityUtils.getCurrentUserId());
        supplier.setUpdatedBy(SecurityUtils.getCurrentUserId());

        // Validate
        supplier.validate();

        // Save
        Supplier savedSupplier = supplierRepository.save(supplier);

        return SupplierMapper.toDTO(savedSupplier);
    }

    @Override
    public SupplierDTO getSupplierById(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với id: " + id));
        return SupplierMapper.toDTO(supplier);
    }

    @Override
    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(SupplierMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierListResponse getList(SupplierListRequest request) {
        List<Supplier> allSuppliers = supplierRepository.findAll();

        // Filter by keyword
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String keyword = request.getKeyword().toLowerCase();
            allSuppliers = allSuppliers.stream()
                    .filter(s -> (s.getCode() != null && s.getCode().toLowerCase().contains(keyword)) ||
                                (s.getName() != null && s.getName().toLowerCase().contains(keyword)) ||
                                (s.getEmail() != null && s.getEmail().toLowerCase().contains(keyword)) ||
                                (s.getPhone() != null && s.getPhone().toLowerCase().contains(keyword)) ||
                                (s.getContactPerson() != null && s.getContactPerson().toLowerCase().contains(keyword)))
                    .collect(Collectors.toList());
        }

        // Filter by status
        if (request.getStatus() != null) {
            allSuppliers = allSuppliers.stream()
                    .filter(s -> s.getStatus() != null && s.getStatus().code().equals(request.getStatus()))
                    .collect(Collectors.toList());
        }

        // Pagination
        int page = request.getPage() != null && request.getPage() > 0 ? request.getPage() - 1 : 0;
        int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10;
        int total = allSuppliers.size();
        int start = page * size;
        int end = Math.min(start + size, total);

        List<Supplier> pagedSuppliers = start < total ? allSuppliers.subList(start, end) : new ArrayList<>();

        SupplierListResponse response = new SupplierListResponse();
        response.setItems(pagedSuppliers.stream().map(SupplierMapper::toDTO).collect(Collectors.toList()));
        response.setPage(request.getPage() != null && request.getPage() > 0 ? request.getPage() : 1);
        response.setSize(size);
        response.setTotal((long) total);

        return response;
    }

    @Override
    @Transactional
    public SupplierDTO updateSupplier(UUID id, UpdateSupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với id: " + id));

        // Check code uniqueness if changed
        if (!supplier.getCode().equals(request.getCode()) && supplierRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã nhà cung cấp đã tồn tại: " + request.getCode());
        }

        // Check email uniqueness if changed
        if (request.getEmail() != null && !request.getEmail().equals(supplier.getEmail()) 
                && supplierRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại: " + request.getEmail());
        }

        // Update fields
        supplier.setCode(request.getCode());
        supplier.setName(request.getName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setTaxCode(request.getTaxCode());
        supplier.setPaymentTerms(request.getPaymentTerms());
        supplier.setRating(request.getRating());
        supplier.setNotes(request.getNotes());
        supplier.setUpdatedBy(SecurityUtils.getCurrentUserId());

        // Validate
        supplier.validate();

        // Save
        Supplier updatedSupplier = supplierRepository.save(supplier);

        return SupplierMapper.toDTO(updatedSupplier);
    }

    @Override
    @Transactional
    public SupplierDTO activateSupplier(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với id: " + id));

        supplier.activate();
        supplier.setUpdatedBy(SecurityUtils.getCurrentUserId());

        Supplier updatedSupplier = supplierRepository.save(supplier);

        return SupplierMapper.toDTO(updatedSupplier);
    }

    @Override
    @Transactional
    public SupplierDTO deactivateSupplier(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với id: " + id));

        supplier.deactivate();
        supplier.setUpdatedBy(SecurityUtils.getCurrentUserId());

        Supplier updatedSupplier = supplierRepository.save(supplier);

        return SupplierMapper.toDTO(updatedSupplier);
    }
}
