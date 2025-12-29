package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.PurchaseRequisitionStatus;
import com.restaurant.ddd.domain.model.PurchaseRequisition;
import com.restaurant.ddd.domain.model.PurchaseRequisitionItem;
import com.restaurant.ddd.domain.respository.PurchaseRequisitionRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.PurchaseRequisitionItemJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.entity.PurchaseRequisitionJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.PurchaseRequisitionDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.mapper.PurchaseRequisitionItemJpaRepository;
import com.restaurant.ddd.infrastructure.persistence.mapper.PurchaseRequisitionJpaRepository;
import com.restaurant.ddd.infrastructure.persistence.specification.PurchaseRequisitionSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of PurchaseRequisitionRepository
 */
@Repository
@Slf4j
public class PurchaseRequisitionRepositoryImpl implements PurchaseRequisitionRepository {

    @Autowired
    private PurchaseRequisitionJpaRepository prJpaRepository;

    @Autowired
    private PurchaseRequisitionItemJpaRepository prItemJpaRepository;

    @Autowired
    private PurchaseRequisitionDataAccessMapper mapper;

    @Override
    @Transactional
    public PurchaseRequisition save(PurchaseRequisition requisition) {
        log.debug("Saving purchase requisition: {}", requisition.getRequisitionCode());
        
        // Save header
        PurchaseRequisitionJpaEntity entity = mapper.toEntity(requisition);
        PurchaseRequisitionJpaEntity savedEntity = prJpaRepository.save(entity);
        
        // Delete existing items and save new ones
        prItemJpaRepository.deleteByRequisitionId(savedEntity.getId());
        
        if (requisition.getItems() != null && !requisition.getItems().isEmpty()) {
            for (PurchaseRequisitionItem item : requisition.getItems()) {
                item.setRequisitionId(savedEntity.getId());
                if (item.getId() == null) {
                    item.setId(UUID.randomUUID());
                }
            }
            List<PurchaseRequisitionItemJpaEntity> itemEntities = mapper.itemsToEntity(requisition.getItems());
            prItemJpaRepository.saveAll(itemEntities);
        }
        
        // Return with items
        PurchaseRequisition result = mapper.toDomain(savedEntity);
        result.setItems(requisition.getItems());
        return result;
    }

    @Override
    public Optional<PurchaseRequisition> findById(UUID id) {
        log.debug("Finding purchase requisition by id: {}", id);
        return prJpaRepository.findById(id)
                .map(entity -> {
                    PurchaseRequisition pr = mapper.toDomain(entity);
                    List<PurchaseRequisitionItemJpaEntity> items = prItemJpaRepository.findByRequisitionId(id);
                    pr.setItems(mapper.itemsToDomain(items));
                    return pr;
                });
    }

    @Override
    public Optional<PurchaseRequisition> findByCode(String code) {
        log.debug("Finding purchase requisition by code: {}", code);
        return prJpaRepository.findByRequisitionCode(code)
                .map(entity -> {
                    PurchaseRequisition pr = mapper.toDomain(entity);
                    List<PurchaseRequisitionItemJpaEntity> items = prItemJpaRepository.findByRequisitionId(entity.getId());
                    pr.setItems(mapper.itemsToDomain(items));
                    return pr;
                });
    }

    @Override
    public List<PurchaseRequisition> findAll() {
        log.debug("Finding all purchase requisitions");
        return prJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseRequisition> findByStatus(PurchaseRequisitionStatus status) {
        log.debug("Finding purchase requisitions by status: {}", status);
        DataStatus dataStatus = DataStatus.fromCode(status.code());
        return prJpaRepository.findByStatus(dataStatus).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseRequisition> findByWarehouseId(UUID warehouseId) {
        log.debug("Finding purchase requisitions by warehouse: {}", warehouseId);
        return prJpaRepository.findByWarehouseId(warehouseId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.debug("Deleting purchase requisition by id: {}", id);
        prItemJpaRepository.deleteByRequisitionId(id);
        prJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return prJpaRepository.existsByRequisitionCode(code);
    }

    @Override
    public String generateNextCode() {
        String prefix = "PR-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        // This is a simplified implementation, in production you might want to use a sequence
        long count = prJpaRepository.count() + 1;
        return prefix + String.format("%03d", count);
    }

    @Override
    public Page<PurchaseRequisition> findAll(
            String keyword,
            UUID warehouseId,
            Integer status,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable) {
        
        return prJpaRepository.findAll(
                PurchaseRequisitionSpecification.buildSpec(keyword, warehouseId, status, fromDate, toDate),
                pageable
        ).map(mapper::toDomain);
    }
}
