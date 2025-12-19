package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.ledger.LedgerPreviewResponse;
import com.restaurant.ddd.application.model.stock.StockInRequest;
import com.restaurant.ddd.application.model.stock.StockTransactionDTO;
import com.restaurant.ddd.application.model.stock.StockTransactionListRequest;
import com.restaurant.ddd.application.model.stock.StockTransactionListResponse;
import com.restaurant.ddd.application.service.StockInAppService;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import com.restaurant.ddd.domain.enums.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/management/stock-in")
@RequiredArgsConstructor
@Tag(name = "Stock In Management", description = "APIs for stock in operations")
@CrossOrigin
public class StockInController {

    private final StockInAppService stockInService;

    @PostMapping
    @Operation(summary = "Create Stock In")
    public ResponseEntity<ResultMessage<StockTransactionDTO>> create(@RequestBody StockInRequest request) {
        try {
            var result = stockInService.create(request);
            if (result.getCode() == ResultCode.SUCCESS) {
                return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
            }
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResultUtil.error(ResultCode.ERROR.code(), "Tạo phiếu nhập thất bại: " + e.getMessage()));
        }
    }

    @PutMapping
    @Operation(summary = "Update Stock In")
    public ResponseEntity<ResultMessage<StockTransactionDTO>> update(
            @RequestParam(name = "id") UUID id,
            @RequestBody StockInRequest request) {
        try {
            var result = stockInService.update(id, request);
            if (result.getCode() == ResultCode.SUCCESS) {
                return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
            }
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResultUtil.error(ResultCode.ERROR.code(), "Cập nhật phiếu nhập thất bại: " + e.getMessage()));
        }
    }

    @GetMapping("/get")
    @Operation(summary = "Get Stock In by ID")
    public ResponseEntity<ResultMessage<StockTransactionDTO>> getById(@RequestParam(name = "id") UUID id) {
        var result = stockInService.getById(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @GetMapping("/list")
    @Operation(summary = "List Stock In transactions")
    public ResponseEntity<ResultMessage<StockTransactionListResponse>> getList(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortDirection", required = false) String sortDirection,
            @RequestParam(name = "warehouseId", required = false) UUID warehouseId,
            @RequestParam(name = "materialId", required = false) UUID materialId,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        StockTransactionListRequest request = new StockTransactionListRequest();
        if (page != null) request.setPage(page);
        if (size != null) request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDirection(sortDirection);
        request.setWarehouseId(warehouseId);
        request.setMaterialId(materialId);
        request.setTransactionType(1); // STOCK_IN
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        
        var result = stockInService.getList(request);
        return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
    }

    @PostMapping("/lock")
    @Operation(summary = "Lock Stock In transaction")
    public ResponseEntity<ResultMessage<String>> lock(@RequestParam(name = "id") UUID id) {
        var result = stockInService.lock(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @PostMapping("/unlock")
    @Operation(summary = "Unlock Stock In transaction")
    public ResponseEntity<ResultMessage<String>> unlock(@RequestParam(name = "id") UUID id) {
        var result = stockInService.unlock(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @GetMapping("/preview-ledger")
    @Operation(summary = "Preview ledger entries before locking")
    public ResponseEntity<ResultMessage<LedgerPreviewResponse>> preview(@RequestParam(name = "id") UUID id) {
        var result = stockInService.previewLedger(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(result.getCode().code(), result.getMessage()));
    }
}
