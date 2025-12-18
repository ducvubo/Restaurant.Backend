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
        // Build Pageable with sorting
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "name";
        org.springframework.data.domain.Sort.Direction direction = 
            "ASC".equalsIgnoreCase(request.getSafeSortDirection()) 
                ? org.springframework.data.domain.Sort.Direction.ASC 
                : org.springframework.data.domain.Sort.Direction.DESC;
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            request.getPageZeroBased(),
            request.getSafeSize(),
            org.springframework.data.domain.Sort.by(direction, sortBy)
        );
        
        // Query with pagination
        org.springframework.data.domain.Page<Supplier> page = supplierRepository.findAll(
            request.getKeyword(),
            request.getStatus(),
            pageable
        );
        
        // Map to DTOs
        List<SupplierDTO> dtos = page.getContent().stream()
            .map(SupplierMapper::toDTO)
            .collect(Collectors.toList());
        
        SupplierListResponse response = new SupplierListResponse();
        response.setItems(dtos);
        response.setTotal(page.getTotalElements());
        response.setPage(request.getPage());
        response.setSize(request.getSafeSize());
        response.setTotalPages(page.getTotalPages());

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
