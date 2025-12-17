package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.model.ledger.LedgerPreviewBatchDTO;
import com.restaurant.ddd.application.model.ledger.LedgerPreviewItemDTO;
import com.restaurant.ddd.application.model.ledger.LedgerPreviewResponse;
import com.restaurant.ddd.application.model.stock.*;
import com.restaurant.ddd.application.service.InventoryLedgerAppService;
import com.restaurant.ddd.application.service.StockTransactionAppService;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.InventoryMethod;
import com.restaurant.ddd.domain.enums.ResultCode;
import com.restaurant.ddd.domain.enums.StockOutType;
import com.restaurant.ddd.domain.enums.StockInType;
import com.restaurant.ddd.domain.model.*;
import com.restaurant.ddd.domain.respository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockTransactionAppServiceImpl implements StockTransactionAppService {

    private final StockInTransactionRepository stockInTransactionRepository;
    private final StockOutTransactionRepository stockOutTransactionRepository;
    private final StockInItemRepository stockInItemRepository;
    private final StockOutItemRepository stockOutItemRepository;
    private final StockOutBatchMappingRepository stockOutBatchMappingRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialRepository materialRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final UnitRepository unitRepository;
    private final InventoryLedgerRepository inventoryLedgerRepository;
    private final InventoryLedgerAppService inventoryLedgerAppService;

    @Override
    @Transactional
    public ResultMessage<StockTransactionDTO> stockIn(StockInRequest request) {
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
                .setTransactionCode(generateTransactionCode("IN"))
                .setWarehouseId(request.getWarehouseId())
                .setSupplierId(request.getSupplierId())
                .setTransactionDate(request.getTransactionDate() != null ? request.getTransactionDate() : LocalDateTime.now())
                .setReferenceNumber(request.getReferenceNumber())
                .setNotes(request.getNotes())
                .setStatus(DataStatus.ACTIVE)
                .setIsLocked(false) // DRAFT - not locked yet
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
        
        // DO NOT create inventory ledger here
        // Ledger will be created when transaction is locked
        
        saved.setTotalAmount(totalAmount);
        stockInTransactionRepository.save(saved);

        return new ResultMessage<>(ResultCode.SUCCESS, "Tạo phiếu nhập nháp thành công. Vui lòng chốt phiếu để ghi sổ cái.", toStockInDTO(saved));
    }

    @Override
    @Transactional
    public ResultMessage<StockTransactionDTO> updateStockIn(UUID id, StockInRequest request) {
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
        existing.setNotes(request.getNotes());
        
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
        
        return new ResultMessage<>(ResultCode.SUCCESS, "Cập nhật phiếu nhập thành công", toStockInDTO(updated));
    }

    @Override
    @Transactional
    public ResultMessage<StockTransactionDTO> stockOut(StockOutRequest request) {
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
        
        // Validate available stock for each item (informational only)
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
                .setTransactionCode(generateTransactionCode("OUT"))
                .setWarehouseId(request.getWarehouseId())
                .setDestinationBranchId(request.getDestinationBranchId())
                .setTransactionDate(request.getTransactionDate() != null ? request.getTransactionDate() : LocalDateTime.now())
                .setReferenceNumber(request.getReferenceNumber())
                .setNotes(request.getNotes())
                .setStatus(DataStatus.ACTIVE)
                .setIsLocked(false) // DRAFT - not locked yet
                .setCreatedDate(LocalDateTime.now())
                // Stock Out Type fields
                .setStockOutType(com.restaurant.ddd.domain.enums.StockOutType.fromCode(request.getStockOutType()))
                .setDestinationWarehouseId(request.getDestinationWarehouseId())
                .setCustomerId(request.getCustomerId())
                .setDisposalReason(request.getDisposalReason());
        
        // Validate domain model
        try {
            transaction.validate();
        } catch (IllegalArgumentException e) {
            return new ResultMessage<>(ResultCode.PARAMS_ERROR, e.getMessage(), null);
        }

        // Calculate total amount (estimated, will be recalculated on lock)
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
                    .setNotes(itemRequest.getNotes());
            
            stockOutItemRepository.save(item);
        }
        
        // DO NOT deduct inventory here
        // Inventory will be deducted when transaction is locked
        
        saved.setTotalAmount(totalAmount);
        stockOutTransactionRepository.save(saved);

        return new ResultMessage<>(ResultCode.SUCCESS, "Tạo phiếu xuất nháp thành công. Vui lòng chốt phiếu để ghi sổ cái.", toStockOutDTO(saved));
    }

    @Override
    @Transactional
    public ResultMessage<StockTransactionDTO> updateStockOut(UUID id, StockOutRequest request) {
        // Find existing transaction
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
            // Delete batch mappings first
            stockOutBatchMappingRepository.deleteByStockOutItemId(oldItem.getId());
        }
        // Delete all items
        stockOutItemRepository.deleteByStockOutTransactionId(id);
        
        // Update transaction
        existing.setWarehouseId(request.getWarehouseId());
        existing.setDestinationBranchId(request.getDestinationBranchId());
        existing.setTransactionDate(request.getTransactionDate());
        existing.setReferenceNumber(request.getReferenceNumber());
        existing.setNotes(request.getNotes());
        
        // Update stock out type fields
        if (request.getStockOutType() != null) {
            existing.setStockOutType(StockOutType.fromCode(request.getStockOutType()));
        }
        existing.setDestinationWarehouseId(request.getDestinationWarehouseId());
        existing.setCustomerId(request.getCustomerId());
        existing.setDisposalReason(request.getDisposalReason());
        
        // Validate domain model
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
                    .setNotes(itemRequest.getNotes());
            
            stockOutItemRepository.save(item);
        }
        
        existing.setTotalAmount(totalAmount);
        StockOutTransaction updated = stockOutTransactionRepository.save(existing);
        
        return new ResultMessage<>(ResultCode.SUCCESS, "Cập nhật phiếu xuất thành công", toStockOutDTO(updated));
    }

    @Override
    @Transactional
    public ResultMessage<String> lockTransaction(UUID id) {
        // Try stock in first
        Optional<StockInTransaction> stockIn = stockInTransactionRepository.findById(id);
        if (stockIn.isPresent()) {
            StockInTransaction transaction = stockIn.get();
            if (Boolean.TRUE.equals(transaction.getIsLocked())) {
                return new ResultMessage<>(ResultCode.ERROR, "Phiếu đã được chốt", null);
            }
            
            // Create inventory ledger for all items
            List<StockInItem> items = stockInItemRepository.findByStockInTransactionId(transaction.getId());
            for (StockInItem item : items) {
                createInventoryLedgerForStockIn(transaction, item);
            }
            
            transaction.setIsLocked(true);
            stockInTransactionRepository.save(transaction);
            return new ResultMessage<>(ResultCode.SUCCESS, "Chốt phiếu nhập thành công. Sổ cái đã được ghi.", null);
        }

        // Try stock out
        Optional<StockOutTransaction> stockOut = stockOutTransactionRepository.findById(id);
        if (stockOut.isPresent()) {
            StockOutTransaction transaction = stockOut.get();
            if (Boolean.TRUE.equals(transaction.getIsLocked())) {
                return new ResultMessage<>(ResultCode.ERROR, "Phiếu đã được chốt", null);
            }
            
            // Validate and deduct inventory for all items
            List<StockOutItem> items = stockOutItemRepository.findByStockOutTransactionId(transaction.getId());
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            for (StockOutItem item : items) {
                // Validate stock availability
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
                
                // Deduct inventory and create ledger + batch mapping
                BigDecimal itemCost = deductFromInventoryAndCreateLedger(transaction, item);
                totalAmount = totalAmount.add(itemCost);
            }
            
            transaction.setIsLocked(true);
            transaction.setTotalAmount(totalAmount);
            stockOutTransactionRepository.save(transaction);
            
            // If internal transfer, auto-create stock-in for destination warehouse
            if (transaction.getStockOutType() != null && 
                transaction.getStockOutType() == StockOutType.INTERNAL_TRANSFER) {
                System.out.println("=== AUTO STOCK-IN: Detected internal transfer, stockOutType=" + transaction.getStockOutType());
                System.out.println("=== AUTO STOCK-IN: Destination warehouse=" + transaction.getDestinationWarehouseId());
                try {
                    createAutoStockInForTransfer(transaction);
                    System.out.println("=== AUTO STOCK-IN: Successfully created auto stock-in");
                } catch (Exception e) {
                    System.err.println("=== AUTO STOCK-IN ERROR: " + e.getMessage());
                    e.printStackTrace();
                    // Rollback will happen automatically due to @Transactional
                    return new ResultMessage<>(ResultCode.ERROR, 
                        "Lỗi khi tạo phiếu nhập tự động: " + e.getMessage(), null);
                }
            } else {
                System.out.println("=== AUTO STOCK-IN: Not internal transfer, stockOutType=" + transaction.getStockOutType());
            }
            
            return new ResultMessage<>(ResultCode.SUCCESS, "Chốt phiếu xuất thành công. Sổ cái đã được ghi.", null);
        }

        return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy phiếu", null);
    }

    @Override
    @Transactional
    public ResultMessage<String> unlockTransaction(UUID id) {
        // Try stock in first
        Optional<StockInTransaction> stockIn = stockInTransactionRepository.findById(id);
        if (stockIn.isPresent()) {
            StockInTransaction transaction = stockIn.get();
            
            // Delete inventory ledger entries for this transaction
            List<InventoryLedger> ledgers = inventoryLedgerRepository.findByTransactionId(id);
            for (InventoryLedger ledger : ledgers) {
                inventoryLedgerRepository.delete(ledger);
            }
            
            transaction.setIsLocked(false);
            stockInTransactionRepository.save(transaction);
            return new ResultMessage<>(ResultCode.SUCCESS, "Mở khóa phiếu nhập thành công. Sổ cái đã được xóa.", null);
        }
        
        // Try stock out
        Optional<StockOutTransaction> stockOut = stockOutTransactionRepository.findById(id);
        if (stockOut.isPresent()) {
            StockOutTransaction transaction = stockOut.get();
            
            // Restore inventory: add back quantities from batch mappings
            List<StockOutBatchMapping> mappings = stockOutBatchMappingRepository.findByStockOutTransactionId(id);
            for (StockOutBatchMapping mapping : mappings) {
                InventoryLedger ledger = inventoryLedgerRepository.findById(mapping.getInventoryLedgerId()).orElse(null);
                if (ledger != null) {
                    // Restore quantity
                    BigDecimal restoredQty = ledger.getRemainingQuantity().add(mapping.getQuantityUsed());
                    ledger.setRemainingQuantity(restoredQty);
                    inventoryLedgerRepository.save(ledger);
                }
                // Delete batch mapping
                stockOutBatchMappingRepository.delete(mapping);
            }
            
            transaction.setIsLocked(false);
            stockOutTransactionRepository.save(transaction);
            return new ResultMessage<>(ResultCode.SUCCESS, "Mở khóa phiếu xuất thành công. Tồn kho đã được hoàn trả.", null);
        }
        
        return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy phiếu", null);
    }

    @Override
    public ResultMessage<StockTransactionDTO> getTransaction(UUID id) {
        // Try stock in first
        Optional<StockInTransaction> stockIn = stockInTransactionRepository.findById(id);
        if (stockIn.isPresent()) {
            return new ResultMessage<>(ResultCode.SUCCESS, "Success", toStockInDTO(stockIn.get()));
        }

        // Try stock out
        Optional<StockOutTransaction> stockOut = stockOutTransactionRepository.findById(id);
        if (stockOut.isPresent()) {
            return new ResultMessage<>(ResultCode.SUCCESS, "Success", toStockOutDTO(stockOut.get()));
        }

        return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy phiếu", null);
    }

    @Override
    public ResultMessage<StockTransactionListResponse> getList(StockTransactionListRequest request) {
        List<StockTransactionDTO> transactions = new ArrayList<>();
        
        // Filter by transaction type
        Integer transactionType = request.getTransactionType();
        
        if (transactionType == null || transactionType == 1) {
            // Get Stock In transactions
            List<StockInTransaction> stockInList = stockInTransactionRepository.findAll();
            transactions.addAll(stockInList.stream()
                    .map(this::toStockInDTO)
                    .collect(Collectors.toList()));
        }
        
        if (transactionType == null || transactionType == 2) {
            // Get Stock Out transactions
            List<StockOutTransaction> stockOutList = stockOutTransactionRepository.findAll();
            transactions.addAll(stockOutList.stream()
                    .map(this::toStockOutDTO)
                    .collect(Collectors.toList()));
        }
        
        // Sort by transaction date descending
        transactions.sort((a, b) -> b.getTransactionDate().compareTo(a.getTransactionDate()));
        
        StockTransactionListResponse response = new StockTransactionListResponse();
        response.setItems(transactions);
        response.setTotal(transactions.size());
        
        return new ResultMessage<>(ResultCode.SUCCESS, "Lấy danh sách giao dịch thành công", response);
    }

    @Override
    public ResultMessage<LedgerPreviewResponse> previewLedger(UUID transactionId) {
        // Try stock out first (most common use case for preview)
        Optional<StockOutTransaction> stockOutOpt = stockOutTransactionRepository.findById(transactionId);
        if (stockOutOpt.isPresent()) {
            StockOutTransaction transaction = stockOutOpt.get();
            
            // Only allow preview for draft transactions
            if (Boolean.TRUE.equals(transaction.getIsLocked())) {
                return new ResultMessage<>(ResultCode.ERROR, "Phiếu đã chốt, không thể xem preview", null);
            }
            
            return previewStockOutLedger(transaction);
        }
        
        // Try stock in
        Optional<StockInTransaction> stockInOpt = stockInTransactionRepository.findById(transactionId);
        if (stockInOpt.isPresent()) {
            StockInTransaction transaction = stockInOpt.get();
            
            if (Boolean.TRUE.equals(transaction.getIsLocked())) {
                return new ResultMessage<>(ResultCode.ERROR, "Phiếu đã chốt, không thể xem preview", null);
            }
            
            return previewStockInLedger(transaction);
        }
        
        return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy phiếu", null);
    }
    
    private ResultMessage<LedgerPreviewResponse> previewStockOutLedger(StockOutTransaction transaction) {
        List<StockOutItem> items = stockOutItemRepository.findByStockOutTransactionId(transaction.getId());
        List<LedgerPreviewItemDTO> previewItems = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;
        
        for (StockOutItem item : items) {
            // Get available batches for this material (FIFO)
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
                
                // Only add batch if we're actually using some quantity from it
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
            
            // Get material name
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
    
    private ResultMessage<LedgerPreviewResponse> previewStockInLedger(StockInTransaction transaction) {
        List<StockInItem> items = stockInItemRepository.findByStockInTransactionId(transaction.getId());
        List<LedgerPreviewItemDTO> previewItems = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;
        
        for (StockInItem item : items) {
            // For stock in, just show what will be added
            String materialName = materialRepository.findById(item.getMaterialId())
                .map(Material::getName)
                .orElse("Unknown");
            
            LedgerPreviewBatchDTO batchPreview = new LedgerPreviewBatchDTO();
            batchPreview.setBatchNumber(transaction.getTransactionCode());
            batchPreview.setQuantityUsed(item.getQuantity());
            batchPreview.setUnitPrice(item.getUnitPrice());
            batchPreview.setTotalAmount(item.getTotalAmount());
            batchPreview.setRemainingAfter(item.getQuantity()); // Will be added
            
            LedgerPreviewItemDTO previewItem = new LedgerPreviewItemDTO();
            previewItem.setMaterialId(item.getMaterialId());
            previewItem.setMaterialName(materialName);
            previewItem.setBatches(List.of(batchPreview));
            previewItem.setTotalQuantity(item.getQuantity());
            previewItem.setTotalAmount(item.getTotalAmount());
            
            previewItems.add(previewItem);
            grandTotal = grandTotal.add(item.getTotalAmount());
        }
        
        LedgerPreviewResponse response = new LedgerPreviewResponse();
        response.setItems(previewItems);
        response.setGrandTotal(grandTotal);
        
        return new ResultMessage<>(ResultCode.SUCCESS, "Preview thành công", response);
    }

    // Helper methods
    private void createInventoryLedgerForStockIn(StockInTransaction transaction, StockInItem item) {
        InventoryLedger ledger = new InventoryLedger()
                .setId(UUID.randomUUID())
                .setWarehouseId(transaction.getWarehouseId())
                .setMaterialId(item.getMaterialId())
                .setTransactionId(transaction.getId())
                .setTransactionCode(transaction.getTransactionCode())
                .setTransactionDate(transaction.getTransactionDate())
                .setInventoryMethod(InventoryMethod.FIFO)
                .setQuantity(item.getQuantity())
                .setUnitId(item.getUnitId())
                .setUnitPrice(item.getUnitPrice())
                .setRemainingQuantity(item.getQuantity())
                .setStatus(DataStatus.ACTIVE)
                .setCreatedDate(LocalDateTime.now());
        
        inventoryLedgerRepository.save(ledger);
    }

    private BigDecimal deductFromInventoryAndCreateLedger(StockOutTransaction transaction, StockOutItem item) {
        // Get available inventory batches (FIFO)
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
            
            // Update batch
            batch.setRemainingQuantity(batch.getRemainingQuantity().subtract(deductQty));
            inventoryLedgerRepository.save(batch);
            
            // Create batch mapping
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

    private StockTransactionDTO toStockInDTO(StockInTransaction transaction) {
        List<StockInItem> items = stockInItemRepository.findByStockInTransactionId(transaction.getId());
        
        StockTransactionDTO dto = new StockTransactionDTO();
        dto.setId(transaction.getId());
        dto.setTransactionCode(transaction.getTransactionCode());
        dto.setWarehouseId(transaction.getWarehouseId());
        dto.setSupplierId(transaction.getSupplierId());
        dto.setStockInType(transaction.getStockInType());
        dto.setRelatedTransactionId(transaction.getRelatedTransactionId());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setReferenceNumber(transaction.getReferenceNumber());
        dto.setNotes(transaction.getNotes());
        dto.setTotalAmount(transaction.getTotalAmount());
        dto.setIsLocked(transaction.getIsLocked());
        dto.setStatus(transaction.getStatus().code());
        dto.setCreatedDate(transaction.getCreatedDate());
        
        // Get warehouse name
        warehouseRepository.findById(transaction.getWarehouseId())
                .ifPresent(wh -> dto.setWarehouseName(wh.getName()));
        
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
        
        // Get related transaction code if exists
        if (transaction.getRelatedTransactionId() != null) {
            stockOutTransactionRepository.findById(transaction.getRelatedTransactionId())
                    .ifPresent(relatedTx -> dto.setRelatedTransactionCode(relatedTx.getTransactionCode()));
        }
        
        // Map items
        dto.setStockInItems(items.stream().map(this::toStockInItemDTO).collect(Collectors.toList()));
        
        return dto;
    }

    private StockTransactionDTO toStockOutDTO(StockOutTransaction transaction) {
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
        dto.setCreatedDate(transaction.getCreatedDate());
        
        // Map stock out type fields
        if (transaction.getStockOutType() != null) {
            dto.setStockOutType(transaction.getStockOutType().code());
            dto.setStockOutTypeName(transaction.getStockOutType().message());
        }
        dto.setDestinationWarehouseId(transaction.getDestinationWarehouseId());
        dto.setCustomerId(transaction.getCustomerId());
        dto.setDisposalReason(transaction.getDisposalReason());
        
        // Get warehouse name
        warehouseRepository.findById(transaction.getWarehouseId())
                .ifPresent(wh -> dto.setWarehouseName(wh.getName()));
        
        // Get destination warehouse name
        if (transaction.getDestinationWarehouseId() != null) {
            warehouseRepository.findById(transaction.getDestinationWarehouseId())
                    .ifPresent(wh -> dto.setDestinationWarehouseName(wh.getName()));
        }
        
        // Get customer name
        if (transaction.getCustomerId() != null) {
            customerRepository.findById(transaction.getCustomerId())
                    .ifPresent(customer -> dto.setCustomerName(customer.getName()));
        }
        
        // Map items
        dto.setStockOutItems(items.stream().map(this::toStockOutItemDTO).collect(Collectors.toList()));
        
        return dto;
    }

    private StockInItemDTO toStockInItemDTO(StockInItem item) {
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

    private StockOutItemDTO toStockOutItemDTO(StockOutItem item) {
        StockOutItemDTO dto = new StockOutItemDTO();
        dto.setId(item.getId());
        dto.setMaterialId(item.getMaterialId());
        dto.setUnitId(item.getUnitId());
        dto.setQuantity(item.getQuantity());
        dto.setNotes(item.getNotes());
        
        // Set material name
        materialRepository.findById(item.getMaterialId()).ifPresent(material -> 
            dto.setMaterialName(material.getName())
        );
        
        // Set unit name
        unitRepository.findById(item.getUnitId()).ifPresent(unit -> 
            dto.setUnitName(unit.getName())
        );
        
        // Load batch mappings and calculate total amount
        List<StockOutBatchMapping> mappings = stockOutBatchMappingRepository.findByStockOutItemId(item.getId());
        dto.setBatchMappings(mappings.stream().map(this::toBatchMappingDTO).collect(Collectors.toList()));
        
        // Calculate total amount from batch mappings (FIFO cost)
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (StockOutBatchMapping mapping : mappings) {
            // Get the inventory ledger to get unit price
            inventoryLedgerRepository.findById(mapping.getInventoryLedgerId()).ifPresent(ledger -> {
                BigDecimal cost = mapping.getQuantityUsed().multiply(ledger.getUnitPrice());
                dto.setTotalAmount((dto.getTotalAmount() != null ? dto.getTotalAmount() : BigDecimal.ZERO).add(cost));
            });
        }
        
        return dto;
    }

    private StockOutBatchMappingDTO toBatchMappingDTO(StockOutBatchMapping mapping) {
        StockOutBatchMappingDTO dto = new StockOutBatchMappingDTO();
        dto.setId(mapping.getId());
        dto.setInventoryLedgerId(mapping.getInventoryLedgerId());
        dto.setQuantityUsed(mapping.getQuantityUsed());
        dto.setUnitPrice(mapping.getUnitPrice());
        
        // Get batch code from inventory ledger
        inventoryLedgerRepository.findById(mapping.getInventoryLedgerId()).ifPresent(ledger -> 
            dto.setBatchNumber(ledger.getTransactionCode())
        );
        
        return dto;
    }
    
    /**
     * Tự động tạo và chốt phiếu nhập kho cho kho đích khi chuyển kho nội bộ
     */
    private void createAutoStockInForTransfer(StockOutTransaction stockOut) {
        if (stockOut.getDestinationWarehouseId() == null) {
            throw new IllegalArgumentException("Không tìm thấy kho đích cho phiếu chuyển kho");
        }
        
        // 1. Create stock-in transaction
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
        stockIn.setStatus(DataStatus.ACTIVE);
        stockIn.setIsLocked(false);
        stockIn.setCreatedBy(stockOut.getCreatedBy());
        stockIn.setCreatedDate(LocalDateTime.now());
        
        StockInTransaction savedStockIn = stockInTransactionRepository.save(stockIn);
        
        // 2. Create stock-in items with unit prices from FIFO batches
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
            inItem.setTotalAmount(outItem.getQuantity().multiply(avgUnitPrice)); // Calculate totalAmount
            inItem.setNotes("Từ phiếu xuất: " + stockOut.getTransactionCode());
            inItem.setCreatedDate(LocalDateTime.now());
            
            stockInItemRepository.save(inItem);
            totalAmount = totalAmount.add(totalCost);
        }
        
        // 3. Lock stock-in and create inventory ledger
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
            ledger.setStatus(DataStatus.ACTIVE);
            ledger.setCreatedDate(LocalDateTime.now());
            
            inventoryLedgerRepository.save(ledger);
        }
    }
    private String generateTransactionCode(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }
}
