package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.InventoryLedgerMapper;
import com.restaurant.ddd.application.model.ledger.InventoryLedgerDTO;
import com.restaurant.ddd.application.model.ledger.InventoryLedgerListRequest;
import com.restaurant.ddd.application.model.ledger.InventoryLedgerListResponse;
import com.restaurant.ddd.application.service.InventoryLedgerAppService;
import com.restaurant.ddd.domain.enums.ResultCode;
import com.restaurant.ddd.domain.model.InventoryLedger;
import com.restaurant.ddd.domain.respository.InventoryLedgerRepository;
import com.restaurant.ddd.domain.respository.MaterialRepository;
import com.restaurant.ddd.domain.respository.WarehouseRepository;
import com.restaurant.ddd.domain.model.ResultMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryLedgerAppServiceImpl implements InventoryLedgerAppService {

    private final InventoryLedgerRepository inventoryLedgerRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialRepository materialRepository;
    private final com.restaurant.ddd.domain.respository.UnitRepository unitRepository;

    @Override
    public ResultMessage<InventoryLedgerListResponse> getList(InventoryLedgerListRequest request) {
        // Validate required parameters
        if (request.getWarehouseId() == null || request.getMaterialId() == null) {
            return new ResultMessage<>(ResultCode.ERROR, "Vui lòng chọn kho và nguyên vật liệu để xem sổ cái", null);
        }
        
        // Build Pageable with sorting
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "transactionDate";
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
        org.springframework.data.domain.Page<InventoryLedger> page = inventoryLedgerRepository.findAll(
            request.getWarehouseId(),
            request.getMaterialId(),
            request.getStartDate(),
            request.getEndDate(),
            pageable
        );
        
        // Map to DTOs
        List<InventoryLedgerDTO> dtos = page.getContent().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        
        InventoryLedgerListResponse response = new InventoryLedgerListResponse();
        response.setItems(dtos);
        response.setTotal(page.getTotalElements());
        response.setPage(request.getPage());
        response.setSize(request.getSafeSize());
        response.setTotalPages(page.getTotalPages());

        return new ResultMessage<>(ResultCode.SUCCESS, "Lấy sổ cái thành công", response);
    }

    @Override
    public ResultMessage<BigDecimal> getCurrentStock(UUID warehouseId, UUID materialId) {
        BigDecimal stock = inventoryLedgerRepository.getCurrentStock(warehouseId, materialId);
        return new ResultMessage<>(ResultCode.SUCCESS, "Lấy tồn kho thành công", stock);
    }

    @Override
    public BigDecimal getAvailableStock(UUID warehouseId, UUID materialId) {
        List<InventoryLedger> availableBatches = inventoryLedgerRepository.findAvailableStock(warehouseId, materialId);
        
        return availableBatches.stream()
                .map(InventoryLedger::getRemainingQuantity)
                .filter(qty -> qty != null && qty.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private InventoryLedgerDTO toDTO(InventoryLedger ledger) {
        InventoryLedgerDTO dto = InventoryLedgerMapper.toDTO(ledger);
        if (dto != null) {
            warehouseRepository.findById(dto.getWarehouseId()).ifPresent(w -> dto.setWarehouseName(w.getName()));
            materialRepository.findById(dto.getMaterialId()).ifPresent(m -> dto.setMaterialName(m.getName()));
            
            // Load unit name
            if (dto.getUnitId() != null) {
                unitRepository.findById(dto.getUnitId()).ifPresent(u -> dto.setUnitName(u.getName()));
            }
            
            // TransactionCode đã có sẵn trong ledger.transactionCode
            dto.setTransactionCode(ledger.getTransactionCode());
        }
        return dto;
    }
}
