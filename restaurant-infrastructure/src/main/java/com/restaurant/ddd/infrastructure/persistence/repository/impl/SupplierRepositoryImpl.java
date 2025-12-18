package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Supplier;
import com.restaurant.ddd.domain.respository.SupplierRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.SupplierJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.SupplierDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.mapper.SupplierJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of SupplierRepository
 */
@Repository
@Slf4j
public class SupplierRepositoryImpl implements SupplierRepository {

    @Autowired
    private SupplierJpaRepository supplierJpaRepository;

    @Autowired
    private SupplierDataAccessMapper supplierDataAccessMapper;

    @Override
    public Supplier save(Supplier supplier) {
        log.debug("Saving supplier: {}", supplier.getCode());
        SupplierJpaEntity entity = supplierDataAccessMapper.toEntity(supplier);
        SupplierJpaEntity savedEntity = supplierJpaRepository.save(entity);
        return supplierDataAccessMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Supplier> findById(UUID id) {
        log.debug("Finding supplier by id: {}", id);
        return supplierJpaRepository.findById(id)
                .map(supplierDataAccessMapper::toDomain);
    }

    @Override
    public Optional<Supplier> findByCode(String code) {
        log.debug("Finding supplier by code: {}", code);
        return supplierJpaRepository.findByCode(code)
                .map(supplierDataAccessMapper::toDomain);
    }

    @Override
    public List<Supplier> findAll() {
        log.debug("Finding all suppliers");
        return supplierJpaRepository.findAll().stream()
                .map(supplierDataAccessMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Supplier> findByStatus(DataStatus status) {
        log.debug("Finding suppliers by status: {}", status);
        return supplierJpaRepository.findByStatus(status).stream()
                .map(supplierDataAccessMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting supplier by id: {}", id);
        supplierJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return supplierJpaRepository.existsByCode(code);
    }

    @Override
    public boolean existsByEmail(String email) {
        return supplierJpaRepository.existsByEmail(email);
    }
    
    @Override
    public org.springframework.data.domain.Page<Supplier> findAll(
            String keyword,
            Integer status,
            org.springframework.data.domain.Pageable pageable) {
        
        return supplierJpaRepository.findAll(
                com.restaurant.ddd.infrastructure.persistence.specification.SupplierSpecification.buildSpec(keyword, status),
                pageable
        ).map(supplierDataAccessMapper::toDomain);
    }
}
