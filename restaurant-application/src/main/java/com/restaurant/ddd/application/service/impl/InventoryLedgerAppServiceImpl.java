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

    @Override
    public ResultMessage<InventoryLedgerListResponse> getList(InventoryLedgerListRequest request) {
        // Fetch logic matching repo methods
        List<InventoryLedger> all;
        if (request.getWarehouseId() != null && request.getMaterialId() != null) {
            all = inventoryLedgerRepository.findByWarehouseAndMaterial(request.getWarehouseId(), request.getMaterialId());
        } else {
             // Fallback or error? For now empty or simple scan if repo supports it
             // Current impl of repo methods is limited. Assuming wrapper handles this or we add more finders.
             // For strict filtering, we need dynamic queries. 
             // Returning empty if not specific enough to prevent huge load
             return new ResultMessage<>(ResultCode.ERROR, "Vui lòng chọn kho và nguyên vật liệu để xem sổ cái", null);
        }

        List<InventoryLedgerDTO> dtos = all.stream()
            .sorted(Comparator.comparing(InventoryLedger::getTransactionDate).reversed())
            .map(this::toDTO)
            .collect(Collectors.toList());

        // Pagination
        int page = request.getPage() != null ? request.getPage() : 1;
        int size = request.getSize() != null ? request.getSize() : 10;
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, dtos.size());
        
        List<InventoryLedgerDTO> pagedDtos;
        if (fromIndex >= dtos.size()) {
            pagedDtos = List.of();
        } else {
            pagedDtos = dtos.subList(fromIndex, toIndex);
        }

        InventoryLedgerListResponse response = new InventoryLedgerListResponse();
        response.setItems(pagedDtos);
        response.setTotal(dtos.size());
        response.setPage(page);
        response.setSize(size);
        response.setTotalPages((int) Math.ceil((double) dtos.size() / size));

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
            // TransactionCode đã có sẵn trong ledger.transactionCode
            dto.setTransactionCode(ledger.getTransactionCode());
        }
        return dto;
    }
}
