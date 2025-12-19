package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.model.adjustment.*;
import com.restaurant.ddd.application.model.ledger.LedgerPreviewBatchDTO;
import com.restaurant.ddd.application.model.ledger.LedgerPreviewItemDTO;
import com.restaurant.ddd.application.model.ledger.LedgerPreviewResponse;
import com.restaurant.ddd.application.service.AdjustmentTransactionAppService;
import com.restaurant.ddd.domain.model.ResultMessage;
import com.restaurant.ddd.domain.enums.*;
import com.restaurant.ddd.domain.model.*;
import com.restaurant.ddd.domain.respository.*;
import com.restaurant.ddd.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdjustmentTransactionAppServiceImpl implements AdjustmentTransactionAppService {

    private final AdjustmentTransactionRepository adjustmentTransactionRepository;
    private final AdjustmentItemRepository adjustmentItemRepository;
    private final AdjustmentBatchMappingRepository adjustmentBatchMappingRepository;
    private final InventoryLedgerRepository inventoryLedgerRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialRepository materialRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResultMessage<AdjustmentTransactionDTO> createAdjustment(AdjustmentTransactionRequest request) {
        // Validation
        if (request.getWarehouseId() == null) {
            return new ResultMessage<>(ResultCode.ERROR, "Kho không được để trống", null);
        }
        if (request.getAdjustmentType() == null) {
            return new ResultMessage<>(ResultCode.ERROR, "Loại điều chỉnh không được để trống", null);
        }
        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Lý do điều chỉnh không được để trống", null);
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Danh sách nguyên liệu không được rỗng", null);
        }

        // Create transaction
        AdjustmentTransaction transaction = new AdjustmentTransaction();
        transaction.setId(UUID.randomUUID());
        transaction.setTransactionCode(generateTransactionCode("ADJ"));
        transaction.setWarehouseId(request.getWarehouseId());
        transaction.setAdjustmentType(AdjustmentType.fromCode(request.getAdjustmentType()));
        transaction.setTransactionDate(request.getTransactionDate() != null ? request.getTransactionDate() : LocalDateTime.now());
        transaction.setReason(request.getReason());
        transaction.setReferenceNumber(request.getReferenceNumber());
        transaction.setNotes(request.getNotes());
        transaction.setPerformedBy(request.getPerformedBy()); // From request
        transaction.setCreatedBy(SecurityUtils.getCurrentUserId()); // Auto-populate
        transaction.setIsLocked(false);
        transaction.setStatus(DataStatus.ACTIVE);
        transaction.setCreatedDate(LocalDateTime.now());

        // Validate stock for DECREASE adjustments
        if (transaction.getAdjustmentType() == AdjustmentType.DECREASE) {
            for (AdjustmentItemRequest itemReq : request.getItems()) {
                BigDecimal currentStock = inventoryLedgerRepository.getCurrentStock(
                    request.getWarehouseId(), 
                    itemReq.getMaterialId()
                );
                if (itemReq.getQuantity().compareTo(currentStock) > 0) {
                    return new ResultMessage<>(ResultCode.ERROR, 
                        "Số lượng giảm vượt quá tồn kho hiện tại", null);
                }
            }
        }

        AdjustmentTransaction saved = adjustmentTransactionRepository.save(transaction);

        // Create items
        for (AdjustmentItemRequest itemReq : request.getItems()) {
            AdjustmentItem item = new AdjustmentItem();
            item.setId(UUID.randomUUID());
            item.setAdjustmentTransactionId(saved.getId());
            item.setMaterialId(itemReq.getMaterialId());
            item.setUnitId(itemReq.getUnitId());
            item.setQuantity(itemReq.getQuantity());
            item.setNotes(itemReq.getNotes());
            item.setCreatedDate(LocalDateTime.now());
            
            adjustmentItemRepository.save(item);
        }

        return new ResultMessage<>(ResultCode.SUCCESS, "Tạo phiếu điều chỉnh thành công", toDTO(saved));
    }

    @Override
    @Transactional
    public ResultMessage<AdjustmentTransactionDTO> updateAdjustment(UUID id, AdjustmentTransactionRequest request) {
        Optional<AdjustmentTransaction> opt = adjustmentTransactionRepository.findById(id);
        if (!opt.isPresent()) {
            return new ResultMessage<>(ResultCode.ERROR, "Phiếu điều chỉnh không tồn tại", null);
        }

        AdjustmentTransaction transaction = opt.get();
        if (transaction.getIsLocked()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không thể sửa phiếu đã chốt", null);
        }

        // Update transaction
        transaction.setWarehouseId(request.getWarehouseId());
        transaction.setAdjustmentType(AdjustmentType.fromCode(request.getAdjustmentType()));
        transaction.setTransactionDate(request.getTransactionDate() != null ? request.getTransactionDate() : transaction.getTransactionDate());
        transaction.setReason(request.getReason());
        transaction.setReferenceNumber(request.getReferenceNumber());
        transaction.setPerformedBy(request.getPerformedBy());
        transaction.setNotes(request.getNotes());
        transaction.setUpdatedBy(SecurityUtils.getCurrentUserId());
        transaction.setUpdatedDate(LocalDateTime.now());

        adjustmentTransactionRepository.save(transaction);

        // Delete old items
        List<AdjustmentItem> oldItems = adjustmentItemRepository.findByAdjustmentTransactionId(id);
        for (AdjustmentItem item : oldItems) {
            adjustmentItemRepository.deleteById(item.getId());
        }

        // Create new items
        for (AdjustmentItemRequest itemReq : request.getItems()) {
            AdjustmentItem item = new AdjustmentItem();
            item.setId(UUID.randomUUID());
            item.setAdjustmentTransactionId(transaction.getId());
            item.setMaterialId(itemReq.getMaterialId());
            item.setUnitId(itemReq.getUnitId());
            item.setQuantity(itemReq.getQuantity());
            item.setNotes(itemReq.getNotes());
            item.setCreatedDate(LocalDateTime.now());
            
            adjustmentItemRepository.save(item);
        }

        return new ResultMessage<>(ResultCode.SUCCESS, "Cập nhật phiếu điều chỉnh thành công", toDTO(transaction));
    }

    @Override
    public ResultMessage<AdjustmentTransactionDTO> getAdjustment(UUID id) {
        Optional<AdjustmentTransaction> transaction = adjustmentTransactionRepository.findById(id);
        if (!transaction.isPresent()) {
            return new ResultMessage<>(ResultCode.ERROR, "Phiếu điều chỉnh không tồn tại", null);
        }
        return new ResultMessage<>(ResultCode.SUCCESS, "Lấy thông tin thành công", toDTO(transaction.get()));
    }

    @Override
    public ResultMessage<AdjustmentListResponse> listAdjustments(AdjustmentListRequest request) {
        // Build Pageable with sorting
        String sortField = request.getSortBy() != null ? request.getSortBy() : "createdDate";
        String sortDirection = request.getSortDirection() != null ? request.getSortDirection() : "desc";
        
        org.springframework.data.domain.Sort.Direction direction = 
            "asc".equalsIgnoreCase(sortDirection) 
                ? org.springframework.data.domain.Sort.Direction.ASC 
                : org.springframework.data.domain.Sort.Direction.DESC;
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            request.getPage() - 1,
            request.getSize(),
            org.springframework.data.domain.Sort.by(direction, sortField)
        );
        
        // Call repository with filters
        org.springframework.data.domain.Page<AdjustmentTransaction> page = adjustmentTransactionRepository.findAll(
            request.getWarehouseId(),
            request.getMaterialId(),
            request.getFromDate(),
            request.getToDate(),
            request.getStatus(),
            pageable
        );
        
        // Map to DTOs
        List<AdjustmentTransactionDTO> dtos = page.getContent().stream()
                .map(this::toListDTO)
                .collect(Collectors.toList());
        
        // Build response
        AdjustmentListResponse response = new AdjustmentListResponse();
        response.setItems(dtos);
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setTotal(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        
        return new ResultMessage<>(ResultCode.SUCCESS, "Lấy danh sách thành công", response);
    }

    @Override
    @Transactional
    public ResultMessage<Void> deleteAdjustment(UUID id) {
        Optional<AdjustmentTransaction> transaction = adjustmentTransactionRepository.findById(id);
        if (!transaction.isPresent()) {
            return new ResultMessage<>(ResultCode.ERROR, "Phiếu điều chỉnh không tồn tại", null);
        }
        if (transaction.get().getIsLocked()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không thể xóa phiếu đã chốt", null);
        }

        // Delete items first
        List<AdjustmentItem> items = adjustmentItemRepository.findByAdjustmentTransactionId(id);
        for (AdjustmentItem item : items) {
            adjustmentItemRepository.deleteById(item.getId());
        }

        adjustmentTransactionRepository.deleteById(id);
        return new ResultMessage<>(ResultCode.SUCCESS, "Xóa phiếu điều chỉnh thành công", null);
    }

    @Override
    @Transactional
    public ResultMessage<Void> lockAdjustment(UUID id) {
        Optional<AdjustmentTransaction> opt = adjustmentTransactionRepository.findById(id);
        if (!opt.isPresent()) {
            return new ResultMessage<>(ResultCode.ERROR, "Phiếu điều chỉnh không tồn tại", null);
        }

        AdjustmentTransaction transaction = opt.get();
        if (transaction.getIsLocked()) {
            return new ResultMessage<>(ResultCode.ERROR, "Phiếu đã được chốt", null);
        }

        // Validate user fields before locking
        if (transaction.getPerformedBy() == null) {
            return new ResultMessage<>(ResultCode.ERROR, "Vui lòng chọn Người Điều Chỉnh trước khi chốt phiếu", null);
        }

        // Lock transaction
        transaction.setIsLocked(true);
        adjustmentTransactionRepository.save(transaction);

        // Create inventory ledger entries
        List<AdjustmentItem> items = adjustmentItemRepository.findByAdjustmentTransactionId(id);
        
        if (transaction.getAdjustmentType() == AdjustmentType.INCREASE) {
            // Điều chỉnh TĂNG: Tạo ledger mới
            for (AdjustmentItem item : items) {
                InventoryLedger ledger = new InventoryLedger();
                ledger.setId(UUID.randomUUID());
                ledger.setWarehouseId(transaction.getWarehouseId());
                ledger.setMaterialId(item.getMaterialId());
                ledger.setTransactionId(transaction.getId());
                ledger.setTransactionCode(transaction.getTransactionCode());
                ledger.setTransactionDate(transaction.getTransactionDate());
                ledger.setQuantity(item.getQuantity());
                ledger.setRemainingQuantity(item.getQuantity());
                ledger.setUnitId(item.getUnitId());
                ledger.setUnitPrice(BigDecimal.ZERO); // Adjustment has no price
                ledger.setInventoryMethod(InventoryMethod.FIFO);
                ledger.setStatus(DataStatus.ACTIVE);
                ledger.setCreatedDate(LocalDateTime.now());
                
                inventoryLedgerRepository.save(ledger);
            }
        } else if (transaction.getAdjustmentType() == AdjustmentType.DECREASE) {
            // Điều chỉnh GIẢM: Dùng FIFO
            for (AdjustmentItem item : items) {
                BigDecimal remainingQty = item.getQuantity();
                
                // Get FIFO batches
                List<InventoryLedger> batches = inventoryLedgerRepository
                        .findAvailableStock(transaction.getWarehouseId(), item.getMaterialId());
                
                for (InventoryLedger batch : batches) {
                    if (remainingQty.compareTo(BigDecimal.ZERO) <= 0) break;
                    
                    BigDecimal deductQty = remainingQty.min(batch.getRemainingQuantity());
                    
                    // Update batch
                    batch.setRemainingQuantity(batch.getRemainingQuantity().subtract(deductQty));
                    inventoryLedgerRepository.save(batch);
                    
                    // Create mapping
                    AdjustmentBatchMapping mapping = new AdjustmentBatchMapping();
                    mapping.setId(UUID.randomUUID());
                    mapping.setAdjustmentItemId(item.getId());
                    mapping.setInventoryLedgerId(batch.getId());
                    mapping.setQuantityUsed(deductQty);
                    adjustmentBatchMappingRepository.save(mapping);
                    
                    remainingQty = remainingQty.subtract(deductQty);
                }
                
                if (remainingQty.compareTo(BigDecimal.ZERO) > 0) {
                    return new ResultMessage<>(ResultCode.ERROR, 
                        "Không đủ tồn kho cho nguyên liệu: " + item.getMaterialId(), null);
                }
            }
        }

        return new ResultMessage<>(ResultCode.SUCCESS, "Chốt phiếu điều chỉnh thành công", null);
    }

    @Override
    @Transactional
    public ResultMessage<Void> unlockAdjustment(UUID id) {
        Optional<AdjustmentTransaction> opt = adjustmentTransactionRepository.findById(id);
        if (!opt.isPresent()) {
            return new ResultMessage<>(ResultCode.ERROR, "Phiếu điều chỉnh không tồn tại", null);
        }

        AdjustmentTransaction transaction = opt.get();
        if (!transaction.getIsLocked()) {
            return new ResultMessage<>(ResultCode.ERROR, "Phiếu chưa được chốt", null);
        }

        // Unlock transaction
        transaction.setIsLocked(false);
        adjustmentTransactionRepository.save(transaction);

        // Revert inventory ledger changes
        List<AdjustmentItem> items = adjustmentItemRepository.findByAdjustmentTransactionId(id);
        
        if (transaction.getAdjustmentType() == AdjustmentType.INCREASE) {
            // Delete ledger entries
            for (AdjustmentItem item : items) {
                List<InventoryLedger> ledgers = inventoryLedgerRepository.findByTransactionId(transaction.getId());
                for (InventoryLedger ledger : ledgers) {
                    inventoryLedgerRepository.delete(ledger);
                }
            }
        } else if (transaction.getAdjustmentType() == AdjustmentType.DECREASE) {
            // Restore batch quantities
            for (AdjustmentItem item : items) {
                List<AdjustmentBatchMapping> mappings = adjustmentBatchMappingRepository.findByAdjustmentItemId(item.getId());
                for (AdjustmentBatchMapping mapping : mappings) {
                    Optional<InventoryLedger> ledgerOpt = inventoryLedgerRepository.findById(mapping.getInventoryLedgerId());
                    if (ledgerOpt.isPresent()) {
                        InventoryLedger ledger = ledgerOpt.get();
                        ledger.setRemainingQuantity(ledger.getRemainingQuantity().add(mapping.getQuantityUsed()));
                        inventoryLedgerRepository.save(ledger);
                    }
                    // Delete mapping - use delete instead of deleteById
                    adjustmentBatchMappingRepository.delete(mapping);
                }
            }
        }

        return new ResultMessage<>(ResultCode.SUCCESS, "Mở khóa phiếu điều chỉnh thành công", null);
    }

    @Override
    public ResultMessage<LedgerPreviewResponse> previewLedger(UUID transactionId) {
        Optional<AdjustmentTransaction> opt = adjustmentTransactionRepository.findById(transactionId);
        if (!opt.isPresent()) {
            return new ResultMessage<>(ResultCode.ERROR, "Phiếu điều chỉnh không tồn tại", null);
        }

        AdjustmentTransaction transaction = opt.get();
        if (transaction.getIsLocked()) {
            return new ResultMessage<>(ResultCode.ERROR, "Phiếu đã chốt, không thể xem preview", null);
        }

        if (transaction.getAdjustmentType() == AdjustmentType.INCREASE) {
            return previewIncreaseAdjustment(transaction);
        } else {
            return previewDecreaseAdjustment(transaction);
        }
    }

    private ResultMessage<LedgerPreviewResponse> previewIncreaseAdjustment(AdjustmentTransaction transaction) {
        List<AdjustmentItem> items = adjustmentItemRepository.findByAdjustmentTransactionId(transaction.getId());
        List<LedgerPreviewItemDTO> previewItems = new ArrayList<>();

        for (AdjustmentItem item : items) {
            String materialName = materialRepository.findById(item.getMaterialId())
                .map(Material::getName)
                .orElse("Unknown");

            LedgerPreviewItemDTO previewItem = new LedgerPreviewItemDTO();
            previewItem.setMaterialId(item.getMaterialId());
            previewItem.setMaterialName(materialName);
            previewItem.setTotalQuantity(item.getQuantity());
            previewItem.setBatches(new ArrayList<>()); // No batches for increase
            previewItem.setTotalAmount(BigDecimal.ZERO);

            previewItems.add(previewItem);
        }

        LedgerPreviewResponse response = new LedgerPreviewResponse();
        response.setItems(previewItems);
        response.setGrandTotal(BigDecimal.ZERO);

        return new ResultMessage<>(ResultCode.SUCCESS, "Xem trước sổ cái thành công", response);
    }

    private ResultMessage<LedgerPreviewResponse> previewDecreaseAdjustment(AdjustmentTransaction transaction) {
        List<AdjustmentItem> items = adjustmentItemRepository.findByAdjustmentTransactionId(transaction.getId());
        List<LedgerPreviewItemDTO> previewItems = new ArrayList<>();

        for (AdjustmentItem item : items) {
            List<InventoryLedger> availableBatches = inventoryLedgerRepository.findAvailableStock(
                transaction.getWarehouseId(),
                item.getMaterialId()
            );

            BigDecimal remainingQty = item.getQuantity();
            List<LedgerPreviewBatchDTO> batchPreviews = new ArrayList<>();

            for (InventoryLedger batch : availableBatches) {
                if (remainingQty.compareTo(BigDecimal.ZERO) <= 0) break;

                BigDecimal deductQty = remainingQty.min(batch.getRemainingQuantity());

                if (deductQty.compareTo(BigDecimal.ZERO) > 0) {
                    LedgerPreviewBatchDTO batchPreview = new LedgerPreviewBatchDTO();
                    batchPreview.setBatchId(batch.getId());
                    batchPreview.setBatchNumber(batch.getTransactionCode());
                    batchPreview.setTransactionDate(batch.getTransactionDate());
                    batchPreview.setQuantityUsed(deductQty);
                    batchPreview.setUnitPrice(BigDecimal.ZERO);
                    batchPreview.setTotalAmount(BigDecimal.ZERO);
                    batchPreview.setRemainingAfter(batch.getRemainingQuantity().subtract(deductQty));

                    batchPreviews.add(batchPreview);
                }

                remainingQty = remainingQty.subtract(deductQty);
            }

            String materialName = materialRepository.findById(item.getMaterialId())
                .map(Material::getName)
                .orElse("Unknown");

            LedgerPreviewItemDTO previewItem = new LedgerPreviewItemDTO();
            previewItem.setMaterialId(item.getMaterialId());
            previewItem.setMaterialName(materialName);
            previewItem.setTotalQuantity(item.getQuantity());
            previewItem.setBatches(batchPreviews);
            previewItem.setTotalAmount(BigDecimal.ZERO);

            previewItems.add(previewItem);
        }

        LedgerPreviewResponse response = new LedgerPreviewResponse();
        response.setItems(previewItems);
        response.setGrandTotal(BigDecimal.ZERO);

        return new ResultMessage<>(ResultCode.SUCCESS, "Xem trước sổ cái thành công", response);
    }

    private String generateTransactionCode(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }

    private AdjustmentTransactionDTO toDTO(AdjustmentTransaction transaction) {
        AdjustmentTransactionDTO dto = new AdjustmentTransactionDTO();
        dto.setId(transaction.getId());
        dto.setTransactionCode(transaction.getTransactionCode());
        dto.setWarehouseId(transaction.getWarehouseId());
        dto.setAdjustmentType(transaction.getAdjustmentType().code());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setReason(transaction.getReason());
        dto.setReferenceNumber(transaction.getReferenceNumber());
        dto.setNotes(transaction.getNotes());
        dto.setIsLocked(transaction.getIsLocked());
        dto.setPerformedBy(transaction.getPerformedBy());
        dto.setCreatedBy(transaction.getCreatedBy());

        // Get warehouse name
        warehouseRepository.findById(transaction.getWarehouseId())
                .ifPresent(w -> dto.setWarehouseName(w.getName()));

        // Get adjustment type name
        dto.setAdjustmentTypeName(transaction.getAdjustmentType().message());
        
        // Load user names
        if (transaction.getPerformedBy() != null) {
            userRepository.findById(transaction.getPerformedBy())
                .ifPresent(user -> dto.setPerformedByName(user.getFullName()));
        }
        if (transaction.getCreatedBy() != null) {
            userRepository.findById(transaction.getCreatedBy())
                .ifPresent(user -> dto.setCreatedByName(user.getFullName()));
        }

        // Get items
        List<AdjustmentItem> items = adjustmentItemRepository.findByAdjustmentTransactionId(transaction.getId());
        List<AdjustmentItemDTO> itemDTOs = items.stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);

        return dto;
    }
    
    // Lightweight DTO conversion for list (without items)
    private AdjustmentTransactionDTO toListDTO(AdjustmentTransaction transaction) {
        AdjustmentTransactionDTO dto = new AdjustmentTransactionDTO();
        dto.setId(transaction.getId());
        dto.setTransactionCode(transaction.getTransactionCode());
        dto.setWarehouseId(transaction.getWarehouseId());
        dto.setAdjustmentType(transaction.getAdjustmentType().code());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setReason(transaction.getReason());
        dto.setReferenceNumber(transaction.getReferenceNumber());
        dto.setNotes(transaction.getNotes());
        dto.setIsLocked(transaction.getIsLocked());
        dto.setPerformedBy(transaction.getPerformedBy());
        dto.setCreatedBy(transaction.getCreatedBy());

        // Get warehouse name
        warehouseRepository.findById(transaction.getWarehouseId())
                .ifPresent(w -> dto.setWarehouseName(w.getName()));

        // Get adjustment type name
        dto.setAdjustmentTypeName(transaction.getAdjustmentType().message());
        
        // Load user names
        if (transaction.getPerformedBy() != null) {
            userRepository.findById(transaction.getPerformedBy())
                .ifPresent(user -> dto.setPerformedByName(user.getFullName()));
        }
        if (transaction.getCreatedBy() != null) {
            userRepository.findById(transaction.getCreatedBy())
                .ifPresent(user -> dto.setCreatedByName(user.getFullName()));
        }
        
        // No items loaded for list view
        return dto;
    }

    private AdjustmentItemDTO toItemDTO(AdjustmentItem item) {
        AdjustmentItemDTO dto = new AdjustmentItemDTO();
        dto.setId(item.getId());
        dto.setMaterialId(item.getMaterialId());
        dto.setUnitId(item.getUnitId());
        dto.setQuantity(item.getQuantity());
        dto.setInventoryLedgerId(item.getInventoryLedgerId());
        dto.setNotes(item.getNotes());

        // Get material name and unit
        materialRepository.findById(item.getMaterialId())
                .ifPresent(m -> {
                    dto.setMaterialName(m.getName());
                    // Get unit name from unit repository
                    unitRepository.findById(item.getUnitId())
                            .ifPresent(u -> dto.setUnitName(u.getName()));
                });

        // Get batch number if this item is from inventory count
        if (item.getInventoryLedgerId() != null) {
            inventoryLedgerRepository.findById(item.getInventoryLedgerId())
                    .ifPresent(ledger -> {
                        String batchNumber = ledger.getBatchNumber();
                        if (batchNumber == null || batchNumber.isEmpty()) {
                            batchNumber = ledger.getTransactionCode() != null 
                                ? ledger.getTransactionCode() 
                                : "BATCH-" + ledger.getId().toString().substring(0, 8);
                        }
                        dto.setBatchNumber(batchNumber);
                    });
        }

        return dto;
    }
}
