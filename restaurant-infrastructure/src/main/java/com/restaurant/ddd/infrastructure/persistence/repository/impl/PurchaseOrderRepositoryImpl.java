package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.PurchaseOrderStatus;
import com.restaurant.ddd.domain.model.PurchaseOrder;
import com.restaurant.ddd.domain.model.PurchaseOrderItem;
import com.restaurant.ddd.domain.respository.PurchaseOrderRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.PurchaseOrderItemJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.entity.PurchaseOrderJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.PurchaseOrderDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.mapper.PurchaseOrderItemJpaRepository;
import com.restaurant.ddd.infrastructure.persistence.mapper.PurchaseOrderJpaRepository;
import com.restaurant.ddd.infrastructure.persistence.specification.PurchaseOrderSpecification;
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
 * Implementation of PurchaseOrderRepository
 */
@Repository
@Slf4j
public class PurchaseOrderRepositoryImpl implements PurchaseOrderRepository {

    @Autowired
    private PurchaseOrderJpaRepository poJpaRepository;

    @Autowired
    private PurchaseOrderItemJpaRepository poItemJpaRepository;

    @Autowired
    private PurchaseOrderDataAccessMapper mapper;

    @Override
    @Transactional
    public PurchaseOrder save(PurchaseOrder po) {
        log.debug("Saving Purchase Order: {}", po.getPoCode());
        
        // Save header
        PurchaseOrderJpaEntity entity = mapper.toEntity(po);
        PurchaseOrderJpaEntity savedEntity = poJpaRepository.save(entity);
        
        // Delete existing items and save new ones
        poItemJpaRepository.deleteByPoId(savedEntity.getId());
        
        if (po.getItems() != null && !po.getItems().isEmpty()) {
            for (PurchaseOrderItem item : po.getItems()) {
                item.setPoId(savedEntity.getId());
                if (item.getId() == null) {
                    item.setId(UUID.randomUUID());
                }
            }
            List<PurchaseOrderItemJpaEntity> itemEntities = mapper.itemsToEntity(po.getItems());
            poItemJpaRepository.saveAll(itemEntities);
        }
        
        // Return with items
        PurchaseOrder result = mapper.toDomain(savedEntity);
        result.setItems(po.getItems());
        return result;
    }

    @Override
    public Optional<PurchaseOrder> findById(UUID id) {
        log.debug("Finding Purchase Order by id: {}", id);
        return poJpaRepository.findById(id)
                .map(entity -> {
                    PurchaseOrder po = mapper.toDomain(entity);
                    List<PurchaseOrderItemJpaEntity> items = poItemJpaRepository.findByPoId(id);
                    po.setItems(mapper.itemsToDomain(items));
                    return po;
                });
    }

    @Override
    public Optional<PurchaseOrder> findByCode(String code) {
        log.debug("Finding Purchase Order by code: {}", code);
        return poJpaRepository.findByPoCode(code)
                .map(entity -> {
                    PurchaseOrder po = mapper.toDomain(entity);
                    List<PurchaseOrderItemJpaEntity> items = poItemJpaRepository.findByPoId(entity.getId());
                    po.setItems(mapper.itemsToDomain(items));
                    return po;
                });
    }

    @Override
    public List<PurchaseOrder> findAll() {
        log.debug("Finding all Purchase Orders");
        return poJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrder> findByStatus(PurchaseOrderStatus status) {
        log.debug("Finding Purchase Orders by status: {}", status);
        DataStatus dataStatus = DataStatus.fromCode(status.code());
        return poJpaRepository.findByStatus(dataStatus).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrder> findBySupplierId(UUID supplierId) {
        log.debug("Finding Purchase Orders by supplier: {}", supplierId);
        return poJpaRepository.findBySupplierId(supplierId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrder> findByWarehouseId(UUID warehouseId) {
        log.debug("Finding Purchase Orders by warehouse: {}", warehouseId);
        return poJpaRepository.findByWarehouseId(warehouseId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrder> findByRfqId(UUID rfqId) {
        log.debug("Finding Purchase Orders by RFQ: {}", rfqId);
        return poJpaRepository.findByRfqId(rfqId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.debug("Deleting Purchase Order by id: {}", id);
        poItemJpaRepository.deleteByPoId(id);
        poJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return poJpaRepository.existsByPoCode(code);
    }

    @Override
    public String generateNextCode() {
        String prefix = "PO-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        long count = poJpaRepository.count() + 1;
        return prefix + String.format("%03d", count);
    }

    @Override
    public Page<PurchaseOrder> findAll(
            String keyword,
            UUID supplierId,
            UUID warehouseId,
            Integer status,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable) {
        
        return poJpaRepository.findAll(
                PurchaseOrderSpecification.buildSpec(keyword, supplierId, warehouseId, status, fromDate, toDate),
                pageable
        ).map(mapper::toDomain);
    }
}
