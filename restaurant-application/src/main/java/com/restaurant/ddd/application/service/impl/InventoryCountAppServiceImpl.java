package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.model.inventorycount.*;
import com.restaurant.ddd.application.service.InventoryCountAppService;
import com.restaurant.ddd.domain.enums.AdjustmentType;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.InventoryCountStatus;
import com.restaurant.ddd.domain.model.*;
import com.restaurant.ddd.domain.respository.*;
import com.restaurant.ddd.infrastructure.persistence.entity.InventoryLedgerJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.repository.InventoryLedgerJpaRepository;
import com.restaurant.ddd.infrastructure.persistence.specification.InventoryCountSpecification;
import com.restaurant.ddd.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryCountAppServiceImpl implements InventoryCountAppService {
    
    private final InventoryCountRepository inventoryCountRepository;
    private final InventoryCountItemRepository inventoryCountItemRepository;
    private final com.restaurant.ddd.infrastructure.persistence.repository.InventoryCountJpaRepository inventoryCountJpaRepository;
    private final InventoryLedgerJpaRepository inventoryLedgerJpaRepository;
    private final AdjustmentTransactionRepository adjustmentTransactionRepository;
    private final AdjustmentItemRepository adjustmentItemRepository;
    private final MaterialRepository materialRepository;
    private final UnitRepository unitRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public InventoryCountDTO create(InventoryCountRequest request) {
        // Generate count code
        String countCode = "IC" + System.currentTimeMillis();
        
        // Create inventory count
        InventoryCount inventoryCount = new InventoryCount();
        inventoryCount.setId(UUID.randomUUID());
        inventoryCount.setCountCode(countCode);
        inventoryCount.setWarehouseId(request.getWarehouseId());
        inventoryCount.setCountDate(request.getCountDate() != null ? request.getCountDate() : LocalDateTime.now());
        inventoryCount.setCountStatus(InventoryCountStatus.DRAFT);
        inventoryCount.setNotes(request.getNotes());
        inventoryCount.setPerformedBy(request.getPerformedBy()); // From request
        inventoryCount.setCreatedBy(SecurityUtils.getCurrentUserId()); // Auto-populate
        inventoryCount.setStatus(DataStatus.ACTIVE);
        inventoryCount.setCreatedDate(LocalDateTime.now());
        
        inventoryCount.validate();
        InventoryCount saved = inventoryCountRepository.save(inventoryCount);
        
        // Create items
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (InventoryCountItemRequest itemReq : request.getItems()) {
                createItem(saved.getId(), itemReq);
            }
        }
        
        return get(saved.getId());
    }
    
    private void createItem(UUID inventoryCountId, InventoryCountItemRequest request) {
        // Load batch info from InventoryLedger
        InventoryLedgerJpaEntity ledger = inventoryLedgerJpaRepository.findById(request.getInventoryLedgerId())
                .orElseThrow(() -> new IllegalArgumentException("Lô hàng không tồn tại"));
        
        InventoryCountItem item = new InventoryCountItem();
        item.setId(UUID.randomUUID());
        item.setInventoryCountId(inventoryCountId);
        item.setMaterialId(request.getMaterialId());
        item.setUnitId(request.getUnitId());
        item.setInventoryLedgerId(request.getInventoryLedgerId());
        item.setBatchNumber(ledger.getBatchNumber());
        item.setTransactionDate(ledger.getTransactionDate());
        item.setSystemQuantity(ledger.getRemainingQuantity());
        item.setActualQuantity(request.getActualQuantity());
        item.calculateDifference();
        item.setNotes(request.getNotes());
        item.setCreatedDate(LocalDateTime.now());
        
        item.validate();
        inventoryCountItemRepository.save(item);
    }
    
    @Override
    @Transactional
    public InventoryCountDTO update(UUID id, InventoryCountRequest request) {
        InventoryCount inventoryCount = inventoryCountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu kiểm kê không tồn tại"));
        
        if (!inventoryCount.canEdit()) {
            throw new IllegalStateException("Không thể sửa phiếu kiểm kê đã hoàn thành hoặc đã hủy");
        }
        
        inventoryCount.setWarehouseId(request.getWarehouseId());
        inventoryCount.setCountDate(request.getCountDate());
        inventoryCount.setPerformedBy(request.getPerformedBy());
        inventoryCount.setNotes(request.getNotes());
        inventoryCount.setUpdatedBy(SecurityUtils.getCurrentUserId());
        inventoryCount.setUpdatedDate(LocalDateTime.now());
        
        inventoryCountRepository.save(inventoryCount);
        
        // Update items - delete old and create new
        inventoryCountItemRepository.deleteByInventoryCountId(id);
        if (request.getItems() != null) {
            for (InventoryCountItemRequest itemReq : request.getItems()) {
                createItem(id, itemReq);
            }
        }
        
        return get(id);
    }
    
    @Override
    public InventoryCountDTO get(UUID id) {
        InventoryCount inventoryCount = inventoryCountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu kiểm kê không tồn tại"));
        
        InventoryCountDTO dto = toDTO(inventoryCount);
        
        // Load warehouse name
        warehouseRepository.findById(inventoryCount.getWarehouseId()).ifPresent(warehouse -> {
            dto.setWarehouseName(warehouse.getName());
        });
        
        // Load item details (material names, unit names, batch numbers)
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            dto.getItems().forEach(itemDto -> {
                // Load material name
                materialRepository.findById(itemDto.getMaterialId()).ifPresent(material -> {
                    itemDto.setMaterialName(material.getName());
                });
                
                // Load unit name
                if (itemDto.getUnitId() != null) {
                    unitRepository.findById(itemDto.getUnitId()).ifPresent(unit -> {
                        itemDto.setUnitName(unit.getName());
                    });
                }
                
                // Load batch number from inventory ledger
                if (itemDto.getInventoryLedgerId() != null) {
                    inventoryLedgerJpaRepository.findById(itemDto.getInventoryLedgerId()).ifPresent(ledger -> {
                        String batchNumber = ledger.getBatchNumber();
                        if (batchNumber == null || batchNumber.isEmpty()) {
                            batchNumber = ledger.getTransactionCode() != null 
                                ? ledger.getTransactionCode() 
                                : "BATCH-" + ledger.getId().toString().substring(0, 8);
                        }
                        itemDto.setBatchNumber(batchNumber);
                    });
                }
            });
        }
        
        return dto;
    }
    
    @Override
    public InventoryCountListResponse list(InventoryCountListRequest request) {
        Specification<com.restaurant.ddd.infrastructure.persistence.entity.InventoryCountJpaEntity> spec = 
                InventoryCountSpecification.withFilters(
                        request.getWarehouseId(),
                        request.getCountStatus() != null ? InventoryCountStatus.fromCode(request.getCountStatus()) : null,
                        request.getFromDate(),
                        request.getToDate(),
                        request.getStatus() != null ? DataStatus.fromCode(request.getStatus()) : null
                );
        
        PageRequest pageRequest = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createdDate")
        );
        
        Page<com.restaurant.ddd.infrastructure.persistence.entity.InventoryCountJpaEntity> page = 
                inventoryCountJpaRepository.findAll(spec, pageRequest);
        
        InventoryCountListResponse response = new InventoryCountListResponse();
        response.setItems(page.getContent().stream()
                .map(entity -> {
                    InventoryCount domain = new InventoryCount();
                    domain.setId(entity.getId());
                    domain.setCountCode(entity.getCountCode());
                    domain.setWarehouseId(entity.getWarehouseId());
                    domain.setCountDate(entity.getCountDate());
                    domain.setCountStatus(entity.getCountStatus());
                    domain.setNotes(entity.getNotes());
                    domain.setAdjustmentTransactionId(entity.getAdjustmentTransactionId());
                    domain.setPerformedBy(entity.getPerformedBy());
                    domain.setStatus(entity.getStatus());
                    domain.setCreatedDate(entity.getCreatedDate());
                    domain.setUpdatedDate(entity.getUpdatedDate());
                    
                    InventoryCountDTO dto = toListDTO(domain);
                    
                    // Load warehouse name
                    warehouseRepository.findById(entity.getWarehouseId()).ifPresent(warehouse -> {
                        dto.setWarehouseName(warehouse.getName());
                    });
                    
                    return dto;
                })
                .collect(Collectors.toList()));
        response.setTotal(page.getTotalElements());
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        
        return response;
    }
    
    // Lightweight DTO conversion for list (without items)
    private InventoryCountDTO toListDTO(InventoryCount domain) {
        InventoryCountDTO dto = new InventoryCountDTO();
        dto.setId(domain.getId());
        dto.setCountCode(domain.getCountCode());
        dto.setWarehouseId(domain.getWarehouseId());
        dto.setCountDate(domain.getCountDate());
        dto.setCountStatus(domain.getCountStatus().code());
        dto.setCountStatusName(domain.getCountStatus().message());
        dto.setNotes(domain.getNotes());
        dto.setAdjustmentTransactionId(domain.getAdjustmentTransactionId());
        dto.setPerformedBy(domain.getPerformedBy());
        dto.setCreatedBy(domain.getCreatedBy());
        dto.setStatus(domain.getStatus() != null ? domain.getStatus().code() : null);
        dto.setCreatedDate(domain.getCreatedDate());
        dto.setUpdatedDate(domain.getUpdatedDate());
        
        // Load user names
        if (domain.getPerformedBy() != null) {
            userRepository.findById(domain.getPerformedBy())
                .ifPresent(user -> dto.setPerformedByName(user.getFullName()));
        }
        if (domain.getCreatedBy() != null) {
            userRepository.findById(domain.getCreatedBy())
                .ifPresent(user -> dto.setCreatedByName(user.getFullName()));
        }
        
        // No items loaded for list view
        return dto;
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        InventoryCount inventoryCount = inventoryCountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu kiểm kê không tồn tại"));
        
        if (!inventoryCount.canEdit()) {
            throw new IllegalStateException("Không thể xóa phiếu kiểm kê đã hoàn thành");
        }
        
        inventoryCount.setStatus(DataStatus.DELETED);
        inventoryCountRepository.save(inventoryCount);
    }
    
    @Override
    public List<BatchInfoDTO> loadBatchesForCount(UUID warehouseId) {
        // Load all batches with remaining quantity > 0
        List<InventoryLedgerJpaEntity> batches = inventoryLedgerJpaRepository
                .findByWarehouseIdAndRemainingQuantityGreaterThan(warehouseId, BigDecimal.ZERO);
        
        return batches.stream()
                .map(this::toBatchInfoDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public InventoryCountDTO complete(UUID id) {
        InventoryCount inventoryCount = inventoryCountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu kiểm kê không tồn tại"));
        
        if (inventoryCount.isCompleted()) {
            throw new IllegalStateException("Phiếu kiểm kê đã hoàn thành");
        }
        
        // Validate user fields before completing
        if (inventoryCount.getPerformedBy() == null) {
            throw new IllegalStateException("Vui lòng chọn Người Kiểm Kê trước khi hoàn thành phiếu");
        }
        
        // Load items
        List<InventoryCountItem> items = inventoryCountItemRepository.findByInventoryCountId(id);
        
        // Filter items with difference
        List<InventoryCountItem> itemsWithDifference = items.stream()
                .filter(InventoryCountItem::hasDifference)
                .collect(Collectors.toList());
        
        if (!itemsWithDifference.isEmpty()) {
            // Create adjustment transaction
            UUID adjustmentId = createAdjustmentFromCount(inventoryCount, itemsWithDifference);
            inventoryCount.setAdjustmentTransactionId(adjustmentId);
        }
        
        // Mark as completed
        inventoryCount.complete();
        inventoryCount.setUpdatedDate(LocalDateTime.now());
        inventoryCountRepository.save(inventoryCount);
        
        return get(id);
    }
    
    private UUID createAdjustmentFromCount(InventoryCount inventoryCount, List<InventoryCountItem> items) {
        // Create adjustment transaction
        AdjustmentTransaction adjustment = new AdjustmentTransaction();
        adjustment.setId(UUID.randomUUID());
        adjustment.setTransactionCode("ADJ-IC-" + System.currentTimeMillis());
        adjustment.setWarehouseId(inventoryCount.getWarehouseId());
        adjustment.setTransactionDate(LocalDateTime.now());
        adjustment.setAdjustmentType(AdjustmentType.INVENTORY_COUNT); // Set adjustment type
        adjustment.setReason("Kiểm kê kho - " + inventoryCount.getCountCode());
        adjustment.setPerformedBy(inventoryCount.getPerformedBy()); // Người kiểm kê
        adjustment.setCreatedBy(SecurityUtils.getCurrentUserId()); // User hiện tại
        adjustment.setIsLocked(true); // Auto-lock to prevent editing and data inconsistency
        adjustment.setStatus(DataStatus.ACTIVE);
        adjustment.setCreatedDate(LocalDateTime.now());
        
        AdjustmentTransaction savedAdjustment = adjustmentTransactionRepository.save(adjustment);
        
        // Create adjustment items for each difference
        for (InventoryCountItem countItem : items) {
            AdjustmentItem adjItem = new AdjustmentItem();
            adjItem.setId(UUID.randomUUID());
            adjItem.setAdjustmentTransactionId(savedAdjustment.getId());
            adjItem.setMaterialId(countItem.getMaterialId());
            adjItem.setUnitId(countItem.getUnitId());
            adjItem.setQuantity(countItem.getDifferenceQuantity().abs());
            adjItem.setInventoryLedgerId(countItem.getInventoryLedgerId()); // ⭐ Set batch ID
            adjItem.setNotes("Từ kiểm kê: " + inventoryCount.getCountCode());
            adjItem.setCreatedDate(LocalDateTime.now());
            
            adjustmentItemRepository.save(adjItem);
            
            // Update remaining quantity of the batch
            updateBatchQuantity(countItem.getInventoryLedgerId(), countItem.getDifferenceQuantity());
        }
        
        return savedAdjustment.getId();
    }
    
    private void updateBatchQuantity(UUID inventoryLedgerId, BigDecimal differenceQuantity) {
        InventoryLedgerJpaEntity ledger = inventoryLedgerJpaRepository.findById(inventoryLedgerId)
                .orElseThrow(() -> new IllegalArgumentException("Lô hàng không tồn tại"));
        
        BigDecimal newQuantity = ledger.getRemainingQuantity().add(differenceQuantity);
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Số lượng tồn kho không thể âm");
        }
        
        ledger.setRemainingQuantity(newQuantity);
        inventoryLedgerJpaRepository.save(ledger);
    }
    
    @Override
    @Transactional
    public InventoryCountDTO cancel(UUID id) {
        InventoryCount inventoryCount = inventoryCountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu kiểm kê không tồn tại"));
        
        inventoryCount.cancel();
        inventoryCount.setUpdatedDate(LocalDateTime.now());
        inventoryCountRepository.save(inventoryCount);
        
        return get(id);
    }
    
    private InventoryCountDTO toDTO(InventoryCount domain) {
        InventoryCountDTO dto = new InventoryCountDTO();
        dto.setId(domain.getId());
        dto.setCountCode(domain.getCountCode());
        dto.setWarehouseId(domain.getWarehouseId());
        dto.setCountDate(domain.getCountDate());
        dto.setCountStatus(domain.getCountStatus().code());
        dto.setCountStatusName(domain.getCountStatus().message());
        dto.setNotes(domain.getNotes());
        dto.setAdjustmentTransactionId(domain.getAdjustmentTransactionId());
        dto.setPerformedBy(domain.getPerformedBy());
        dto.setCreatedBy(domain.getCreatedBy());
        dto.setStatus(domain.getStatus() != null ? domain.getStatus().code() : null);
        dto.setCreatedDate(domain.getCreatedDate());
        dto.setUpdatedDate(domain.getUpdatedDate());
        
        // Load user names
        if (domain.getPerformedBy() != null) {
            userRepository.findById(domain.getPerformedBy())
                .ifPresent(user -> dto.setPerformedByName(user.getFullName()));
        }
        if (domain.getCreatedBy() != null) {
            userRepository.findById(domain.getCreatedBy())
                .ifPresent(user -> dto.setCreatedByName(user.getFullName()));
        }
        
        // Load items
        List<InventoryCountItem> items = inventoryCountItemRepository.findByInventoryCountId(domain.getId());
        dto.setItems(items.stream().map(this::toItemDTO).collect(Collectors.toList()));
        
        return dto;
    }
    
    private InventoryCountItemDTO toItemDTO(InventoryCountItem item) {
        InventoryCountItemDTO dto = new InventoryCountItemDTO();
        dto.setId(item.getId());
        dto.setInventoryCountId(item.getInventoryCountId());
        dto.setMaterialId(item.getMaterialId());
        dto.setUnitId(item.getUnitId());
        dto.setInventoryLedgerId(item.getInventoryLedgerId());
        dto.setBatchNumber(item.getBatchNumber());
        dto.setTransactionDate(item.getTransactionDate());
        dto.setSystemQuantity(item.getSystemQuantity());
        dto.setActualQuantity(item.getActualQuantity());
        dto.setDifferenceQuantity(item.getDifferenceQuantity());
        dto.setNotes(item.getNotes());
        dto.setCreatedDate(item.getCreatedDate());
        return dto;
    }
    
    private BatchInfoDTO toBatchInfoDTO(InventoryLedgerJpaEntity entity) {
        BatchInfoDTO dto = new BatchInfoDTO();
        dto.setInventoryLedgerId(entity.getId());
        dto.setMaterialId(entity.getMaterialId());
        
        // Generate batch number if null
        String batchNumber = entity.getBatchNumber();
        if (batchNumber == null || batchNumber.isEmpty()) {
            batchNumber = entity.getTransactionCode() != null ? entity.getTransactionCode() : "BATCH-" + entity.getId().toString().substring(0, 8);
        }
        dto.setBatchNumber(batchNumber);
        
        dto.setTransactionDate(entity.getTransactionDate());
        dto.setRemainingQuantity(entity.getRemainingQuantity());
        
        // Load material name and unit info
        materialRepository.findById(entity.getMaterialId()).ifPresent(material -> {
            dto.setMaterialName(material.getName());
        });
        
        // Load unit name from entity's unitId
        if (entity.getUnitId() != null) {
            dto.setUnitId(entity.getUnitId());
            unitRepository.findById(entity.getUnitId()).ifPresent(unit -> {
                dto.setUnitName(unit.getName());
            });
        }
        
        return dto;
    }
}
