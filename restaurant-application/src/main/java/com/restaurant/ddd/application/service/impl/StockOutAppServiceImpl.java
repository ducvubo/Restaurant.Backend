package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.model.ledger.LedgerPreviewBatchDTO;
import com.restaurant.ddd.application.model.ledger.LedgerPreviewItemDTO;
import com.restaurant.ddd.application.model.ledger.LedgerPreviewResponse;
import com.restaurant.ddd.application.model.stock.*;
import com.restaurant.ddd.application.service.InventoryLedgerAppService;
import com.restaurant.ddd.application.service.StockOutAppService;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.ResultCode;
import com.restaurant.ddd.domain.enums.StockOutType;
import com.restaurant.ddd.domain.enums.StockInType;
import com.restaurant.ddd.domain.enums.InventoryMethod;
import com.restaurant.ddd.domain.model.*;
import com.restaurant.ddd.domain.respository.*;
import com.restaurant.ddd.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockOutAppServiceImpl implements StockOutAppService {

    private final StockOutTransactionRepository stockOutTransactionRepository;
    private final StockOutItemRepository stockOutItemRepository;
    private final StockOutBatchMappingRepository stockOutBatchMappingRepository;
    private final StockInTransactionRepository stockInTransactionRepository;
    private final StockInItemRepository stockInItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialRepository materialRepository;
    private final CustomerRepository customerRepository;
    private final UnitRepository unitRepository;
    private final InventoryLedgerRepository inventoryLedgerRepository;
    private final InventoryLedgerAppService inventoryLedgerAppService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResultMessage<StockTransactionDTO> create(StockOutRequest request) {
        // Validate items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Danh sách nguyên liệu không được rỗng", null);
        }
        
        // Validate warehouse exists
        if (!warehouseRepository.findById(request.getWarehouseId()).isPresent()) {
            return new ResultMessage<>(ResultCode.ERROR, "Kho không tồn tại", null);
        }
        
        // Validate stock out type
        if (request.getStockOutType() == null) {
            return new ResultMessage<>(ResultCode.ERROR, "Loại xuất kho không được để trống", null);
        }
        
        // Validate available stock for each item
        for (StockOutItemRequest itemRequest : request.getItems()) {
            BigDecimal availableStock = inventoryLedgerAppService.getAvailableStock(
                    request.getWarehouseId(), 
                    itemRequest.getMaterialId()
            );
            
            if (availableStock.compareTo(itemRequest.getQuantity()) < 0) {
                Material material = materialRepository.findById(itemRequest.getMaterialId()).orElse(null);
                String materialName = material != null ? material.getName() : itemRequest.getMaterialId().toString();
                return new ResultMessage<>(
                        ResultCode.ERROR, 
                        String.format("Không đủ tồn kho cho '%s'. Tồn kho hiện tại: %s, Yêu cầu xuất: %s", 
                                materialName, availableStock, itemRequest.getQuantity()), 
                        null
                );
            }
        }

        // Create stock out transaction (DRAFT - not locked)
        StockOutTransaction transaction = new StockOutTransaction()
                .setId(UUID.randomUUID())
                .setTransactionCode(generateTransactionCode("OUT"))
                .setWarehouseId(request.getWarehouseId())
                .setDestinationBranchId(request.getDestinationBranchId())
                .setTransactionDate(request.getTransactionDate() != null ? request.getTransactionDate() : LocalDateTime.now())
                .setReferenceNumber(request.getReferenceNumber())
                .setNotes(request.getNotes())
                .setIssuedBy(request.getIssuedBy())
                .setReceivedBy(request.getReceivedBy())
                .setCreatedBy(SecurityUtils.getCurrentUserId())
                .setStatus(DataStatus.ACTIVE)
                .setIsLocked(false)
                .setCreatedDate(LocalDateTime.now())
                .setStockOutType(StockOutType.fromCode(request.getStockOutType()))
                .setDestinationWarehouseId(request.getDestinationWarehouseId())
                .setCustomerId(request.getCustomerId())
                .setDisposalReason(request.getDisposalReason());
        
        // Validate domain model
        try {
            transaction.validate();
        } catch (IllegalArgumentException e) {
            return new ResultMessage<>(ResultCode.PARAMS_ERROR, e.getMessage(), null);
        }

        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        StockOutTransaction saved = stockOutTransactionRepository.save(transaction);

        // Create items
        for (StockOutItemRequest itemRequest : request.getItems()) {
            StockOutItem item = new StockOutItem()
                    .setId(UUID.randomUUID())
                    .setStockOutTransactionId(saved.getId())
                    .setMaterialId(itemRequest.getMaterialId())
                    .setUnitId(itemRequest.getUnitId())
                    .setQuantity(itemRequest.getQuantity())
                    .setUnitPrice(itemRequest.getUnitPrice())
                    .setTotalAmount(itemRequest.getTotalAmount())
                    .setNotes(itemRequest.getNotes());
            
            stockOutItemRepository.save(item);
            
            if (item.getTotalAmount() != null) {
                totalAmount = totalAmount.add(item.getTotalAmount());
            }
        }
        
        saved.setTotalAmount(totalAmount);
        stockOutTransactionRepository.save(saved);

        return new ResultMessage<>(ResultCode.SUCCESS, "Tạo phiếu xuất nháp thành công. Vui lòng chốt phiếu để ghi sổ cái.", toDTO(saved));
    }

    @Override
    @Transactional
    public ResultMessage<StockTransactionDTO> update(UUID id, StockOutRequest request) {
        Optional<StockOutTransaction> existingOpt = stockOutTransactionRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy phiếu xuất", null);
        }
        
        StockOutTransaction existing = existingOpt.get();
        if (Boolean.TRUE.equals(existing.getIsLocked())) {
            return new ResultMessage<>(ResultCode.ERROR, "Không thể sửa phiếu đã chốt", null);
        }
        
        // Delete old items and batch mappings
        List<StockOutItem> oldItems = stockOutItemRepository.findByStockOutTransactionId(id);
        for (StockOutItem oldItem : oldItems) {
            stockOutBatchMappingRepository.deleteByStockOutItemId(oldItem.getId());
        }
        stockOutItemRepository.deleteByStockOutTransactionId(id);
        
        // Update transaction
        existing.setWarehouseId(request.getWarehouseId());
        existing.setDestinationBranchId(request.getDestinationBranchId());
        existing.setTransactionDate(request.getTransactionDate());
        existing.setReferenceNumber(request.getReferenceNumber());
        existing.setIssuedBy(request.getIssuedBy());
        existing.setReceivedBy(request.getReceivedBy());
        existing.setNotes(request.getNotes());
        existing.setUpdatedBy(SecurityUtils.getCurrentUserId());
        existing.setUpdatedDate(LocalDateTime.now());
        
        if (request.getStockOutType() != null) {
            existing.setStockOutType(StockOutType.fromCode(request.getStockOutType()));
        }
        existing.setDestinationWarehouseId(request.getDestinationWarehouseId());
        existing.setCustomerId(request.getCustomerId());
        existing.setDisposalReason(request.getDisposalReason());
        
        try {
            existing.validate();
        } catch (IllegalArgumentException e) {
            return new ResultMessage<>(ResultCode.PARAMS_ERROR, e.getMessage(), null);
        }
        
        // Create new items
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (StockOutItemRequest itemRequest : request.getItems()) {
            StockOutItem item = new StockOutItem()
                    .setId(UUID.randomUUID())
                    .setStockOutTransactionId(existing.getId())
                    .setMaterialId(itemRequest.getMaterialId())
                    .setUnitId(itemRequest.getUnitId())
                    .setQuantity(itemRequest.getQuantity())
                    .setUnitPrice(itemRequest.getUnitPrice())
                    .setTotalAmount(itemRequest.getTotalAmount())
                    .setNotes(itemRequest.getNotes());
            
            stockOutItemRepository.save(item);
            
            if (item.getTotalAmount() != null) {
                totalAmount = totalAmount.add(item.getTotalAmount());
            }
        }
        
        existing.setTotalAmount(totalAmount);
        StockOutTransaction updated = stockOutTransactionRepository.save(existing);
        
        return new ResultMessage<>(ResultCode.SUCCESS, "Cập nhật phiếu xuất thành công", toDTO(updated));
    }

    @Override
    public ResultMessage<StockTransactionDTO> getById(UUID id) {
        Optional<StockOutTransaction> transaction = stockOutTransactionRepository.findById(id);
        if (transaction.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy phiếu xuất", null);
        }
        return new ResultMessage<>(ResultCode.SUCCESS, "Success", toDTO(transaction.get()));
    }

    @Override
    public ResultMessage<StockTransactionListResponse> getList(StockTransactionListRequest request) {
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "transactionDate";
        Sort.Direction direction = "ASC".equalsIgnoreCase(request.getSafeSortDirection()) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        
        Pageable pageable = PageRequest.of(
            request.getPageZeroBased(),
            request.getSafeSize(),
            Sort.by(direction, sortBy)
        );
        
        Page<StockOutTransaction> page = stockOutTransactionRepository.findAll(
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
        
        return new ResultMessage<>(ResultCode.SUCCESS, "Lấy danh sách phiếu xuất thành công", response);
    }

    @Override
    @Transactional
    public ResultMessage<String> lock(UUID id) {
        Optional<StockOutTransaction> transactionOpt = stockOutTransactionRepository.findById(id);
        if (transactionOpt.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy phiếu xuất", null);
        }
        
        StockOutTransaction transaction = transactionOpt.get();
        if (Boolean.TRUE.equals(transaction.getIsLocked())) {
            return new ResultMessage<>(ResultCode.ERROR, "Phiếu đã được chốt", null);
        }
        
        // Validate and deduct inventory for all items
        List<StockOutItem> items = stockOutItemRepository.findByStockOutTransactionId(transaction.getId());
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (StockOutItem item : items) {
            BigDecimal availableStock = inventoryLedgerAppService.getAvailableStock(
                    transaction.getWarehouseId(), 
                    item.getMaterialId()
            );
            
            if (availableStock.compareTo(item.getQuantity()) < 0) {
                Material material = materialRepository.findById(item.getMaterialId()).orElse(null);
                String materialName = material != null ? material.getName() : item.getMaterialId().toString();
                return new ResultMessage<>(
                        ResultCode.ERROR, 
                        String.format("Không đủ tồn kho để chốt phiếu. '%s': Tồn=%s, Cần=%s", 
                                materialName, availableStock, item.getQuantity()), 
                        null
                );
            }
            
            // Deduct from inventory (updates existing ledger batches)
            // Does NOT create new ledger entry - only reduces remainingQuantity
            BigDecimal itemCost = deductFromInventory(transaction, item);
            totalAmount = totalAmount.add(itemCost);
        }
        
        transaction.setIsLocked(true);
        transaction.setTotalAmount(totalAmount);
        stockOutTransactionRepository.save(transaction);
        
        // Auto-create stock-in for internal transfer
        if (transaction.getStockOutType() == StockOutType.INTERNAL_TRANSFER) {
            try {
                createAutoStockIn(transaction);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi tạo phiếu nhập tự động: " + e.getMessage(), e);
            }
        }
        
        return new ResultMessage<>(ResultCode.SUCCESS, "Chốt phiếu xuất thành công. Tồn kho đã được cập nhật.", null);
    }

    @Override
    @Transactional
    public ResultMessage<String> unlock(UUID id) {
        Optional<StockOutTransaction> transactionOpt = stockOutTransactionRepository.findById(id);
        if (transactionOpt.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy phiếu xuất", null);
        }
        
        StockOutTransaction transaction = transactionOpt.get();
        
        // Restore inventory
        List<StockOutBatchMapping> mappings = stockOutBatchMappingRepository.findByStockOutTransactionId(id);
        for (StockOutBatchMapping mapping : mappings) {
            InventoryLedger ledger = inventoryLedgerRepository.findById(mapping.getInventoryLedgerId()).orElse(null);
            if (ledger != null) {
                BigDecimal restoredQty = ledger.getRemainingQuantity().add(mapping.getQuantityUsed());
                ledger.setRemainingQuantity(restoredQty);
                inventoryLedgerRepository.save(ledger);
            }
            stockOutBatchMappingRepository.delete(mapping);
        }
        
        transaction.setIsLocked(false);
        stockOutTransactionRepository.save(transaction);
        
        return new ResultMessage<>(ResultCode.SUCCESS, "Mở khóa phiếu xuất thành công. Tồn kho đã được hoàn trả.", null);
    }

    @Override
    public ResultMessage<LedgerPreviewResponse> previewLedger(UUID id) {
        Optional<StockOutTransaction> transactionOpt = stockOutTransactionRepository.findById(id);
        if (transactionOpt.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy phiếu xuất", null);
        }
        
        StockOutTransaction transaction = transactionOpt.get();
        if (Boolean.TRUE.equals(transaction.getIsLocked())) {
            return new ResultMessage<>(ResultCode.ERROR, "Phiếu đã chốt, không thể xem preview", null);
        }
        
        List<StockOutItem> items = stockOutItemRepository.findByStockOutTransactionId(transaction.getId());
        List<LedgerPreviewItemDTO> previewItems = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;
        
        for (StockOutItem item : items) {
            List<InventoryLedger> availableBatches = inventoryLedgerRepository.findAvailableStock(
                transaction.getWarehouseId(),
                item.getMaterialId()
            );
            
            BigDecimal remainingQty = item.getQuantity();
            List<LedgerPreviewBatchDTO> batchPreviews = new ArrayList<>();
            BigDecimal itemTotal = BigDecimal.ZERO;
            
            for (InventoryLedger batch : availableBatches) {
                if (remainingQty.compareTo(BigDecimal.ZERO) <= 0) break;
                
                BigDecimal deductQty = remainingQty.min(batch.getRemainingQuantity());
                
                if (deductQty.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal cost = deductQty.multiply(batch.getUnitPrice());
                    
                    LedgerPreviewBatchDTO batchPreview = new LedgerPreviewBatchDTO();
                    batchPreview.setBatchId(batch.getId());
                    batchPreview.setBatchNumber(batch.getTransactionCode());
                    batchPreview.setTransactionDate(batch.getTransactionDate());
                    batchPreview.setQuantityUsed(deductQty);
                    batchPreview.setUnitPrice(batch.getUnitPrice());
                    batchPreview.setTotalAmount(cost);
                    batchPreview.setRemainingAfter(batch.getRemainingQuantity().subtract(deductQty));
                    
                    batchPreviews.add(batchPreview);
                    itemTotal = itemTotal.add(cost);
                }
                
                remainingQty = remainingQty.subtract(deductQty);
            }
            
            String materialName = materialRepository.findById(item.getMaterialId())
                .map(Material::getName)
                .orElse("Unknown");
            
            LedgerPreviewItemDTO previewItem = new LedgerPreviewItemDTO();
            previewItem.setMaterialId(item.getMaterialId());
            previewItem.setMaterialName(materialName);
            previewItem.setBatches(batchPreviews);
            previewItem.setTotalQuantity(item.getQuantity());
            previewItem.setTotalAmount(itemTotal);
            
            previewItems.add(previewItem);
            grandTotal = grandTotal.add(itemTotal);
        }
        
        LedgerPreviewResponse response = new LedgerPreviewResponse();
        response.setItems(previewItems);
        response.setGrandTotal(grandTotal);
        
        return new ResultMessage<>(ResultCode.SUCCESS, "Preview thành công", response);
    }

    // Helper methods
    private BigDecimal deductFromInventory(StockOutTransaction transaction, StockOutItem item) {
        List<InventoryLedger> availableBatches = inventoryLedgerRepository.findAvailableStock(
                transaction.getWarehouseId(),
                item.getMaterialId()
        );

        BigDecimal remainingQty = item.getQuantity();
        BigDecimal totalCost = BigDecimal.ZERO;

        for (InventoryLedger batch : availableBatches) {
            if (remainingQty.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal deductQty = batch.getRemainingQuantity().min(remainingQty);
            BigDecimal cost = deductQty.multiply(batch.getUnitPrice());
            
            batch.setRemainingQuantity(batch.getRemainingQuantity().subtract(deductQty));
            inventoryLedgerRepository.save(batch);
            
            StockOutBatchMapping mapping = new StockOutBatchMapping()
                    .setId(UUID.randomUUID())
                    .setStockOutItemId(item.getId())
                    .setInventoryLedgerId(batch.getId())
                    .setQuantityUsed(deductQty)
                    .setUnitPrice(batch.getUnitPrice())
                    .setCreatedDate(LocalDateTime.now());
            stockOutBatchMappingRepository.save(mapping);
            
            remainingQty = remainingQty.subtract(deductQty);
            totalCost = totalCost.add(cost);
        }

        if (remainingQty.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Không đủ tồn kho cho nguyên liệu: " + item.getMaterialId());
        }

        return totalCost;
    }

    private void createAutoStockIn(StockOutTransaction stockOut) {
        if (stockOut.getDestinationWarehouseId() == null) {
            throw new IllegalArgumentException("Không tìm thấy kho đích cho phiếu chuyển kho");
        }
        
        StockInTransaction stockIn = new StockInTransaction();
        stockIn.setId(UUID.randomUUID());
        stockIn.setTransactionCode("IN-AUTO-" + stockOut.getTransactionCode());
        stockIn.setWarehouseId(stockOut.getDestinationWarehouseId());
        stockIn.setSupplierId(null);
        stockIn.setStockInType(StockInType.INTERNAL_TRANSFER.code());
        stockIn.setRelatedTransactionId(stockOut.getId());
        stockIn.setTransactionDate(stockOut.getTransactionDate());
        stockIn.setReferenceNumber("Tự động từ: " + stockOut.getTransactionCode());
        stockIn.setNotes("Phiếu nhập tự động từ chuyển kho nội bộ: " + stockOut.getTransactionCode());
        stockIn.setPerformedBy(stockOut.getPerformedBy());
        stockIn.setReceivedBy(stockOut.getReceivedBy());
        stockIn.setStatus(DataStatus.ACTIVE);
        stockIn.setIsLocked(false);
        stockIn.setCreatedBy(stockOut.getCreatedBy());
        stockIn.setCreatedDate(LocalDateTime.now());
        
        StockInTransaction savedStockIn = stockInTransactionRepository.save(stockIn);
        
        List<StockOutItem> stockOutItems = stockOutItemRepository.findByStockOutTransactionId(stockOut.getId());
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (StockOutItem outItem : stockOutItems) {
            List<StockOutBatchMapping> mappings = stockOutBatchMappingRepository.findByStockOutItemId(outItem.getId());
            
            BigDecimal totalCost = BigDecimal.ZERO;
            BigDecimal totalQty = BigDecimal.ZERO;
            
            for (StockOutBatchMapping mapping : mappings) {
                Optional<InventoryLedger> ledger = inventoryLedgerRepository.findById(mapping.getInventoryLedgerId());
                if (ledger.isPresent()) {
                    BigDecimal cost = mapping.getQuantityUsed().multiply(ledger.get().getUnitPrice());
                    totalCost = totalCost.add(cost);
                    totalQty = totalQty.add(mapping.getQuantityUsed());
                }
            }
            
            BigDecimal avgUnitPrice = totalQty.compareTo(BigDecimal.ZERO) > 0 
                ? totalCost.divide(totalQty, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            
            StockInItem inItem = new StockInItem();
            inItem.setId(UUID.randomUUID());
            inItem.setStockInTransactionId(savedStockIn.getId());
            inItem.setMaterialId(outItem.getMaterialId());
            inItem.setUnitId(outItem.getUnitId());
            inItem.setQuantity(outItem.getQuantity());
            inItem.setUnitPrice(avgUnitPrice);
            inItem.setTotalAmount(outItem.getQuantity().multiply(avgUnitPrice));
            inItem.setNotes("Từ phiếu xuất: " + stockOut.getTransactionCode());
            inItem.setCreatedDate(LocalDateTime.now());
            
            stockInItemRepository.save(inItem);
            totalAmount = totalAmount.add(totalCost);
        }
        
        savedStockIn.setTotalAmount(totalAmount);
        savedStockIn.setIsLocked(true);
        stockInTransactionRepository.save(savedStockIn);
        
        List<StockInItem> stockInItems = stockInItemRepository.findByStockInTransactionId(savedStockIn.getId());
        for (StockInItem item : stockInItems) {
            InventoryLedger ledger = new InventoryLedger();
            ledger.setId(UUID.randomUUID());
            ledger.setWarehouseId(savedStockIn.getWarehouseId());
            ledger.setMaterialId(item.getMaterialId());
            ledger.setTransactionId(savedStockIn.getId());
            ledger.setTransactionCode(savedStockIn.getTransactionCode());
            ledger.setTransactionDate(savedStockIn.getTransactionDate());
            ledger.setQuantity(item.getQuantity());
            ledger.setRemainingQuantity(item.getQuantity());
            ledger.setUnitPrice(item.getUnitPrice());
            ledger.setInventoryMethod(InventoryMethod.FIFO);
            ledger.setStatus(DataStatus.ACTIVE);
            ledger.setCreatedDate(LocalDateTime.now());
            
            inventoryLedgerRepository.save(ledger);
        }
    }

    private StockTransactionDTO toDTO(StockOutTransaction transaction) {
        List<StockOutItem> items = stockOutItemRepository.findByStockOutTransactionId(transaction.getId());
        
        StockTransactionDTO dto = new StockTransactionDTO();
        dto.setId(transaction.getId());
        dto.setTransactionCode(transaction.getTransactionCode());
        dto.setWarehouseId(transaction.getWarehouseId());
        dto.setDestinationBranchId(transaction.getDestinationBranchId());
        dto.setTransactionType(2); // STOCK_OUT
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setReferenceNumber(transaction.getReferenceNumber());
        dto.setNotes(transaction.getNotes());
        dto.setTotalAmount(transaction.getTotalAmount());
        dto.setIsLocked(transaction.getIsLocked());
        dto.setStatus(transaction.getStatus().code());
        dto.setCreatedBy(transaction.getCreatedBy());
        dto.setIssuedBy(transaction.getIssuedBy());
        dto.setReceivedBy(transaction.getReceivedBy());
        dto.setCreatedDate(transaction.getCreatedDate());
        
        if (transaction.getStockOutType() != null) {
            dto.setStockOutType(transaction.getStockOutType().code());
            dto.setStockOutTypeName(transaction.getStockOutType().message());
        }
        dto.setDestinationWarehouseId(transaction.getDestinationWarehouseId());
        dto.setCustomerId(transaction.getCustomerId());
        dto.setDisposalReason(transaction.getDisposalReason());
        
        warehouseRepository.findById(transaction.getWarehouseId())
                .ifPresent(wh -> dto.setWarehouseName(wh.getName()));
        
        if (transaction.getCreatedBy() != null) {
            userRepository.findById(transaction.getCreatedBy())
                .ifPresent(user -> dto.setCreatedByName(user.getFullName()));
        }
        if (transaction.getIssuedBy() != null) {
            userRepository.findById(transaction.getIssuedBy())
                .ifPresent(user -> dto.setIssuedByName(user.getFullName()));
        }
        if (transaction.getReceivedBy() != null) {
            userRepository.findById(transaction.getReceivedBy())
                .ifPresent(user -> dto.setReceivedByName(user.getFullName()));
        }
        
        if (transaction.getDestinationWarehouseId() != null) {
            warehouseRepository.findById(transaction.getDestinationWarehouseId())
                    .ifPresent(wh -> dto.setDestinationWarehouseName(wh.getName()));
        }
        
        if (transaction.getCustomerId() != null) {
            customerRepository.findById(transaction.getCustomerId())
                    .ifPresent(customer -> dto.setCustomerName(customer.getName()));
        }
        
        dto.setStockOutItems(items.stream().map(this::toItemDTO).collect(Collectors.toList()));
        
        return dto;
    }

    private StockOutItemDTO toItemDTO(StockOutItem item) {
        StockOutItemDTO dto = new StockOutItemDTO();
        dto.setId(item.getId());
        dto.setMaterialId(item.getMaterialId());
        dto.setUnitId(item.getUnitId());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalAmount(item.getTotalAmount());
        dto.setNotes(item.getNotes());
        
        materialRepository.findById(item.getMaterialId()).ifPresent(material -> 
            dto.setMaterialName(material.getName())
        );
        
        unitRepository.findById(item.getUnitId()).ifPresent(unit -> 
            dto.setUnitName(unit.getName())
        );
        
        List<StockOutBatchMapping> mappings = stockOutBatchMappingRepository.findByStockOutItemId(item.getId());
        dto.setBatchMappings(mappings.stream().map(this::toBatchMappingDTO).collect(Collectors.toList()));
        
        return dto;
    }

    private StockOutBatchMappingDTO toBatchMappingDTO(StockOutBatchMapping mapping) {
        StockOutBatchMappingDTO dto = new StockOutBatchMappingDTO();
        dto.setId(mapping.getId());
        dto.setInventoryLedgerId(mapping.getInventoryLedgerId());
        dto.setQuantityUsed(mapping.getQuantityUsed());
        dto.setUnitPrice(mapping.getUnitPrice());
        
        inventoryLedgerRepository.findById(mapping.getInventoryLedgerId()).ifPresent(ledger -> 
            dto.setBatchNumber(ledger.getTransactionCode())
        );
        
        return dto;
    }

    private String generateTransactionCode(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }
}
