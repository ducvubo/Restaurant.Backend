package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.ledger.InventoryLedgerListRequest;
import com.restaurant.ddd.application.model.ledger.InventoryLedgerListResponse;
import com.restaurant.ddd.application.service.InventoryLedgerAppService;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/management/inventory-ledger")
@RequiredArgsConstructor
@Tag(name = "Inventory Ledger Management", description = "APIs for inventory reports")
@CrossOrigin
public class InventoryLedgerController {

    private final InventoryLedgerAppService inventoryLedgerAppService;

    @GetMapping("/get")
    @Operation(summary = "Get Inventory Ledger (History)")
    public ResponseEntity<ResultMessage<InventoryLedgerListResponse>> list(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "warehouseId", required = true) UUID warehouseId,
            @RequestParam(name = "materialId", required = true) UUID materialId,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        InventoryLedgerListRequest request = new InventoryLedgerListRequest();
        request.setPage(page);
        request.setSize(size);
        request.setWarehouseId(warehouseId);
        request.setMaterialId(materialId);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        
        var result = inventoryLedgerAppService.getList(request);
        return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
    }

    @GetMapping("/current-stock")
    @Operation(summary = "Get Current Stock Quantity")
    public ResponseEntity<ResultMessage<BigDecimal>> getCurrentStock(
            @RequestParam(name = "warehouseId", required = true) UUID warehouseId,
            @RequestParam(name = "materialId", required = true) UUID materialId
    ) {
        var result = inventoryLedgerAppService.getCurrentStock(warehouseId, materialId);
        return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
    }

    @GetMapping("/available-stock")
    @Operation(summary = "Get available stock for a material in a warehouse")
    public ResponseEntity<ResultMessage<BigDecimal>> getAvailableStock(
            @RequestParam(name = "warehouseId") UUID warehouseId,
            @RequestParam(name = "materialId") UUID materialId
    ) {
        BigDecimal availableStock = inventoryLedgerAppService.getAvailableStock(warehouseId, materialId);
        return ResponseEntity.ok(ResultUtil.data(availableStock, "Lấy tồn kho thành công"));
    }
}
