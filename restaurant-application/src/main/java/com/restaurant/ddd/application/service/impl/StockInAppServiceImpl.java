package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.model.ledger.LedgerPreviewBatchDTO;
import com.restaurant.ddd.application.model.ledger.LedgerPreviewItemDTO;
import com.restaurant.ddd.application.model.ledger.LedgerPreviewResponse;
import com.restaurant.ddd.application.model.stock.*;
import com.restaurant.ddd.application.service.StockInAppService;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.InventoryMethod;
import com.restaurant.ddd.domain.enums.ResultCode;
import com.restaurant.ddd.domain.enums.StockInType;
import com.restaurant.ddd.domain.model.*;
import com.restaurant.ddd.domain.respository.*;
import com.restaurant.ddd.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockInAppServiceImpl implements StockInAppService {

    private final StockInTransactionRepository stockInTransactionRepository;
    private final StockInItemRepository stockInItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialRepository materialRepository;
    private final SupplierRepository supplierRepository;
    private final UnitRepository unitRepository;
    private final InventoryLedgerRepository inventoryLedgerRepository;
    private final UserRepository userRepository;
    private final com.restaurant.ddd.application.service.UnitConversionService unitConversionService;

    @Override
    @Transactional
    public ResultMessage<StockTransactionDTO> create(StockInRequest request) {
        // Validate items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Danh sách nguyên liệu không được rỗng", null);
        }
        
        // Validate warehouse exists
        if (!warehouseRepository.findById(request.getWarehouseId()).isPresent()) {
            return new ResultMessage<>(ResultCode.ERROR, "Kho không tồn tại", null);
        }
        
        // Create stock in transaction (DRAFT - not locked)
        StockInTransaction transaction = new StockInTransaction()
                .setId(UUID.randomUUID())
                .setTransactionCode(generateTransactionCode("IN"))
                .setWarehouseId(request.getWarehouseId())
                .setSupplierId(request.getSupplierId())
                .setStockInType(StockInType.EXTERNAL.code())
                .setTransactionDate(request.getTransactionDate() != null ? request.getTransactionDate() : LocalDateTime.now())
                .setReferenceNumber(request.getReferenceNumber())
                .setNotes(request.getNotes())
                .setReceivedBy(request.getReceivedBy())
                .setCreatedBy(SecurityUtils.getCurrentUserId())
                .setStatus(DataStatus.ACTIVE)
                .setIsLocked(false)
                .setCreatedDate(LocalDateTime.now());
        
        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (StockInItemRequest itemRequest : request.getItems()) {
            BigDecimal itemTotal = itemRequest.getQuantity().multiply(itemRequest.getUnitPrice());
            totalAmount = totalAmount.add(itemTotal);
        }
        transaction.setTotalAmount(totalAmount);
        
        // Save transaction
        StockInTransaction saved = stockInTransactionRepository.save(transaction);
        
        // Create items
        for (StockInItemRequest itemRequest : request.getItems()) {
            StockInItem item = new StockInItem()
                    .setId(UUID.randomUUID())
                    .setStockInTransactionId(saved.getId())
                    .setMaterialId(itemRequest.getMaterialId())
                    .setUnitId(itemRequest.getUnitId())
                    .setQuantity(itemRequest.getQuantity())
                    .setUnitPrice(itemRequest.getUnitPrice())
                    .setTotalAmount(itemRequest.getQuantity().multiply(itemRequest.getUnitPrice()))
                    .setNotes(itemRequest.getNotes());
            
            stockInItemRepository.save(item);
        }

        return new ResultMessage<>(ResultCode.SUCCESS, "Tạo phiếu nhập nháp thành công. Vui lòng chốt phiếu để ghi sổ cái.", toDTO(saved));
    }

    @Override
    @Transactional
    public ResultMessage<StockTransactionDTO> update(UUID id, StockInRequest request) {
        // Find existing transaction
        Optional<StockInTransaction> existingOpt = stockInTransactionRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy phiếu nhập", null);
        }
        
        StockInTransaction existing = existingOpt.get();
        if (Boolean.TRUE.equals(existing.getIsLocked())) {
            return new ResultMessage<>(ResultCode.ERROR, "Không thể sửa phiếu đã chốt", null);
        }
        
        // Delete old items
        stockInItemRepository.deleteByStockInTransactionId(id);
        
        // Update transaction
        existing.setWarehouseId(request.getWarehouseId());
        existing.setSupplierId(request.getSupplierId());
        existing.setTransactionDate(request.getTransactionDate());
        existing.setReferenceNumber(request.getReferenceNumber());
        existing.setReceivedBy(request.getReceivedBy());
        existing.setStockInType(request.getStockInType());
        existing.setNotes(request.getNotes());
        existing.setUpdatedBy(SecurityUtils.getCurrentUserId());
        existing.setUpdatedDate(LocalDateTime.now());
        
        // Create new items
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (StockInItemRequest itemRequest : request.getItems()) {
            StockInItem item = new StockInItem()
                    .setId(UUID.randomUUID())
                    .setStockInTransactionId(existing.getId())
                    .setMaterialId(itemRequest.getMaterialId())
                    .setUnitId(itemRequest.getUnitId())
                    .setQuantity(itemRequest.getQuantity())
                    .setUnitPrice(itemRequest.getUnitPrice())
                    .setTotalAmount(itemRequest.getQuantity().multiply(itemRequest.getUnitPrice()))
                    .setNotes(itemRequest.getNotes());
            
            stockInItemRepository.save(item);
            totalAmount = totalAmount.add(item.getTotalAmount());
        }
        
        existing.setTotalAmount(totalAmount);
        StockInTransaction updated = stockInTransactionRepository.save(existing);
        
        return new ResultMessage<>(ResultCode.SUCCESS, "Cập nhật phiếu nhập thành công", toDTO(updated));
    }

    @Override
    public ResultMessage<StockTransactionDTO> getById(UUID id) {
        Optional<StockInTransaction> transaction = stockInTransactionRepository.findById(id);
        if (transaction.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy phiếu nhập", null);
        }
        return new ResultMessage<>(ResultCode.SUCCESS, "Success", toDTO(transaction.get()));
    }

    @Override
    public ResultMessage<StockTransactionListResponse> getList(StockTransactionListRequest request) {
        // Build Pageable with sorting
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "transactionDate";
        Sort.Direction direction = "ASC".equalsIgnoreCase(request.getSafeSortDirection()) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        
        Pageable pageable = PageRequest.of(
            request.getPageZeroBased(),
            request.getSafeSize(),
            Sort.by(direction, sortBy)
        );
        
        Page<StockInTransaction> page = stockInTransactionRepository.findAll(
            request.getWarehouseId(),
            request.getMaterialId(),
            request.getStartDate(),
            request.getEndDate(),
            pageable
        );
        
        List<StockTransactionDTO> transactions = page.getContent().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        
        StockTransactionListResponse response = new StockTransactionListResponse();
        response.setItems(transactions);
        response.setTotal(page.getTotalElements());
        response.setPage(request.getPage());
        response.setSize(request.getSafeSize());
        response.setTotalPages(page.getTotalPages());
        
        return new ResultMessage<>(ResultCode.SUCCESS, "Lấy danh sách phiếu nhập thành công", response);
    }

    @Override
    @Transactional
    public ResultMessage<String> lock(UUID id) {
        Optional<StockInTransaction> transactionOpt = stockInTransactionRepository.findById(id);
        if (transactionOpt.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy phiếu nhập", null);
        }
        
        StockInTransaction transaction = transactionOpt.get();
        if (Boolean.TRUE.equals(transaction.getIsLocked())) {
            return new ResultMessage<>(ResultCode.ERROR, "Phiếu đã được chốt", null);
        }
        
        // Create inventory ledger for all items
        List<StockInItem> items = stockInItemRepository.findByStockInTransactionId(transaction.getId());
        for (StockInItem item : items) {
            createInventoryLedger(transaction, item);
        }
        
        transaction.setIsLocked(true);
        stockInTransactionRepository.save(transaction);
        
        return new ResultMessage<>(ResultCode.SUCCESS, "Chốt phiếu nhập thành công. Sổ cái đã được ghi.", null);
    }

    @Override
    @Transactional
    public ResultMessage<String> unlock(UUID id) {
        Optional<StockInTransaction> transactionOpt = stockInTransactionRepository.findById(id);
        if (transactionOpt.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy phiếu nhập", null);
        }
        
        StockInTransaction transaction = transactionOpt.get();
        
        // Delete inventory ledger entries for this transaction
        List<InventoryLedger> ledgers = inventoryLedgerRepository.findByTransactionId(id);
        for (InventoryLedger ledger : ledgers) {
            inventoryLedgerRepository.delete(ledger);
        }
        
        transaction.setIsLocked(false);
        stockInTransactionRepository.save(transaction);
        
        return new ResultMessage<>(ResultCode.SUCCESS, "Mở khóa phiếu nhập thành công. Sổ cái đã được xóa.", null);
    }

    @Override
    public ResultMessage<LedgerPreviewResponse> previewLedger(UUID id) {
        Optional<StockInTransaction> transactionOpt = stockInTransactionRepository.findById(id);
        if (transactionOpt.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy phiếu nhập", null);
        }
        
        StockInTransaction transaction = transactionOpt.get();
        if (Boolean.TRUE.equals(transaction.getIsLocked())) {
            return new ResultMessage<>(ResultCode.ERROR, "Phiếu đã chốt, không thể xem preview", null);
        }
        
        List<StockInItem> items = stockInItemRepository.findByStockInTransactionId(transaction.getId());
        List<LedgerPreviewItemDTO> previewItems = items.stream().map(item -> {
            String materialName = materialRepository.findById(item.getMaterialId())
                .map(Material::getName)
                .orElse("Unknown");
            
            LedgerPreviewBatchDTO batchPreview = new LedgerPreviewBatchDTO();
            batchPreview.setBatchNumber(transaction.getTransactionCode());
            batchPreview.setQuantityUsed(item.getQuantity());
            batchPreview.setUnitPrice(item.getUnitPrice());
            batchPreview.setTotalAmount(item.getTotalAmount());
            batchPreview.setRemainingAfter(item.getQuantity());
            
            LedgerPreviewItemDTO previewItem = new LedgerPreviewItemDTO();
            previewItem.setMaterialId(item.getMaterialId());
            previewItem.setMaterialName(materialName);
            previewItem.setBatches(List.of(batchPreview));
            previewItem.setTotalQuantity(item.getQuantity());
            previewItem.setTotalAmount(item.getTotalAmount());
            
            return previewItem;
        }).collect(Collectors.toList());
        
        BigDecimal grandTotal = items.stream()
            .map(StockInItem::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        LedgerPreviewResponse response = new LedgerPreviewResponse();
        response.setItems(previewItems);
        response.setGrandTotal(grandTotal);
        
        return new ResultMessage<>(ResultCode.SUCCESS, "Preview thành công", response);
    }

    // Helper methods
    private void createInventoryLedger(StockInTransaction transaction, StockInItem item) {
        // Get base unit for material
        UUID baseUnitId = unitConversionService.getBaseUnit(item.getMaterialId());
        
        // Get conversion factor
        BigDecimal conversionFactor = unitConversionService.getConversionFactor(
            item.getUnitId(), baseUnitId);
        
        // Convert quantity to base unit
        BigDecimal baseQuantity = item.getQuantity().multiply(conversionFactor);
        
        log.info("[CREATE_LEDGER] Creating ledger for material: {}", item.getMaterialId());
        log.info("[CREATE_LEDGER] originalUnitId: {}, originalQty: {}", item.getUnitId(), item.getQuantity());
        log.info("[CREATE_LEDGER] baseUnitId: {}, conversionFactor: {}, baseQty: {}", baseUnitId, conversionFactor, baseQuantity);
        
        InventoryLedger ledger = new InventoryLedger()
                .setId(UUID.randomUUID())
                .setWarehouseId(transaction.getWarehouseId())
                .setMaterialId(item.getMaterialId())
                .setTransactionId(transaction.getId())
                .setTransactionCode(transaction.getTransactionCode())
                .setTransactionDate(transaction.getTransactionDate())
                .setInventoryMethod(InventoryMethod.FIFO)
                
                // Original info (user input)
                .setOriginalUnitId(item.getUnitId())
                .setOriginalQuantity(item.getQuantity())
                
                // Base unit info (snapshot)
                .setBaseUnitId(baseUnitId)
                .setConversionFactor(conversionFactor)
                
                // Converted quantity
                .setQuantity(baseQuantity)
                .setUnitId(baseUnitId)  // Store as base unit
                .setUnitPrice(item.getUnitPrice())
                .setRemainingQuantity(baseQuantity)
                .setStatus(DataStatus.ACTIVE)
                .setCreatedDate(LocalDateTime.now());
        
        log.info("[CREATE_LEDGER] Before save - ledger.originalUnitId: {}, baseUnitId: {}, conversionFactor: {}", 
            ledger.getOriginalUnitId(), ledger.getBaseUnitId(), ledger.getConversionFactor());
        
        inventoryLedgerRepository.save(ledger);
        
        log.info("[CREATE_LEDGER] Saved ledger with id: {}", ledger.getId());
    }

    private StockTransactionDTO toDTO(StockInTransaction transaction) {
        List<StockInItem> items = stockInItemRepository.findByStockInTransactionId(transaction.getId());
        
        StockTransactionDTO dto = new StockTransactionDTO();
        dto.setId(transaction.getId());
        dto.setTransactionCode(transaction.getTransactionCode());
        dto.setWarehouseId(transaction.getWarehouseId());
        dto.setSupplierId(transaction.getSupplierId());
        dto.setStockInType(transaction.getStockInType());
        dto.setRelatedTransactionId(transaction.getRelatedTransactionId());
        dto.setTransactionType(1); // STOCK_IN
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setReferenceNumber(transaction.getReferenceNumber());
        dto.setNotes(transaction.getNotes());
        dto.setTotalAmount(transaction.getTotalAmount());
        dto.setIsLocked(transaction.getIsLocked());
        dto.setStatus(transaction.getStatus().code());
        dto.setCreatedBy(transaction.getCreatedBy());
        dto.setReceivedBy(transaction.getReceivedBy());
        dto.setCreatedDate(transaction.getCreatedDate());
        
        // Get warehouse name
        warehouseRepository.findById(transaction.getWarehouseId())
                .ifPresent(wh -> dto.setWarehouseName(wh.getName()));
        
        // Load user names
        if (transaction.getCreatedBy() != null) {
            userRepository.findById(transaction.getCreatedBy())
                .ifPresent(user -> dto.setCreatedByName(user.getFullName()));
        }
        if (transaction.getReceivedBy() != null) {
            userRepository.findById(transaction.getReceivedBy())
                .ifPresent(user -> dto.setReceivedByName(user.getFullName()));
        }
        
        // Get supplier name
        if (transaction.getSupplierId() != null) {
            supplierRepository.findById(transaction.getSupplierId())
                    .ifPresent(sup -> dto.setSupplierName(sup.getName()));
        }
        
        // Get stock in type name
        if (transaction.getStockInType() != null) {
            try {
                StockInType type = StockInType.fromCode(transaction.getStockInType());
                dto.setStockInTypeName(type.message());
            } catch (IllegalArgumentException e) {
                dto.setStockInTypeName("Unknown");
            }
        }
        
        // Map items
        dto.setStockInItems(items.stream().map(this::toItemDTO).collect(Collectors.toList()));
        
        return dto;
    }

    private StockInItemDTO toItemDTO(StockInItem item) {
        StockInItemDTO dto = new StockInItemDTO();
        dto.setId(item.getId());
        dto.setMaterialId(item.getMaterialId());
        dto.setUnitId(item.getUnitId());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalAmount(item.getTotalAmount());
        dto.setNotes(item.getNotes());
        
        // Set material name
        materialRepository.findById(item.getMaterialId()).ifPresent(material -> 
            dto.setMaterialName(material.getName())
        );
        
        // Set unit name
        unitRepository.findById(item.getUnitId()).ifPresent(unit -> 
            dto.setUnitName(unit.getName())
        );
        
        return dto;
    }

    private String generateTransactionCode(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }
}
