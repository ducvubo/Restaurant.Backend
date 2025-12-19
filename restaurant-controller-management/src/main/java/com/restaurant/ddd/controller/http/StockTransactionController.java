package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.ledger.LedgerPreviewResponse;
import com.restaurant.ddd.application.model.stock.*;
import com.restaurant.ddd.application.service.StockTransactionAppService;
import com.restaurant.ddd.application.service.PdfExportService;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import com.restaurant.ddd.domain.enums.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/management/stock-transactions")
@RequiredArgsConstructor
@Tag(name = "Stock Transaction Management", description = "APIs for stock in/out")
@CrossOrigin
@Slf4j
public class StockTransactionController {

    private final StockTransactionAppService stockTransactionAppService;
    private final PdfExportService pdfExportService;

    @PostMapping("/in")
    @Operation(summary = "Stock In")
    public ResponseEntity<ResultMessage<StockTransactionDTO>> stockIn(@RequestBody StockInRequest request) {
        try {
            var result = stockTransactionAppService.stockIn(request);
            if (result.getCode() == ResultCode.SUCCESS) {
                return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
            }
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResultUtil.error(ResultCode.ERROR.code(), "Nhập kho thất bại: " + e.getMessage()));
        }
    }

    @PutMapping("/in")
    @Operation(summary = "Update Stock In")
    public ResponseEntity<ResultMessage<StockTransactionDTO>> updateStockIn(
            @RequestParam(name = "id") UUID id,
            @RequestBody StockInRequest request) {
        try {
            var result = stockTransactionAppService.updateStockIn(id, request);
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

    @PostMapping("/out")
    @Operation(summary = "Stock Out")
    public ResponseEntity<ResultMessage<StockTransactionDTO>> stockOut(@RequestBody StockOutRequest request) {
        try {
            var result = stockTransactionAppService.stockOut(request);
            if (result.getCode() == ResultCode.SUCCESS) {
                return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
            }
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResultUtil.error(ResultCode.ERROR.code(), "Xuất kho thất bại: " + e.getMessage()));
        }
    }

    @PutMapping("/out")
    @Operation(summary = "Update Stock Out")
    public ResponseEntity<ResultMessage<StockTransactionDTO>> updateStockOut(
            @RequestParam(name = "id") UUID id,
            @RequestBody StockOutRequest request) {
        try {
            var result = stockTransactionAppService.updateStockOut(id, request);
            if (result.getCode() == ResultCode.SUCCESS) {
                return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
            }
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResultUtil.error(ResultCode.ERROR.code(), "Cập nhật phiếu xuất thất bại: " + e.getMessage()));
        }
    }

    @GetMapping("/get")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<ResultMessage<StockTransactionDTO>> get(@RequestParam(name = "id") UUID id) {
        var result = stockTransactionAppService.getTransaction(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @GetMapping("/list")
    @Operation(summary = "List transactions")
    public ResponseEntity<ResultMessage<StockTransactionListResponse>> list(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortDirection", required = false) String sortDirection,
            @RequestParam(name = "warehouseId", required = false) UUID warehouseId,
            @RequestParam(name = "materialId", required = false) UUID materialId,
            @RequestParam(name = "transactionType", required = false) Integer transactionType,
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
        request.setTransactionType(transactionType);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        
        var result = stockTransactionAppService.getList(request);
        return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
    }

    @PostMapping("/lock")
    @Operation(summary = "Lock transaction (chốt phiếu)")
    public ResponseEntity<ResultMessage<String>> lockTransaction(@RequestParam(name = "id") UUID id) {
        var result = stockTransactionAppService.lockTransaction(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @PostMapping("/unlock")
    @Operation(summary = "Unlock transaction (mở khóa phiếu)")
    public ResponseEntity<ResultMessage<String>> unlockTransaction(@RequestParam(name = "id") UUID id) {
        var result = stockTransactionAppService.unlockTransaction(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }
    
    @GetMapping("/preview-ledger")
    @Operation(summary = "Preview ledger entries before locking")
    public ResponseEntity<ResultMessage<LedgerPreviewResponse>> previewLedger(@RequestParam(name = "id") UUID id) {
        var result = stockTransactionAppService.previewLedger(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(result.getCode().code(), result.getMessage()));
    }
    
    @GetMapping("/export-pdf")
    @Operation(summary = "Export stock transaction to PDF")
    public ResponseEntity<byte[]> exportPdf(@RequestParam(name = "id") UUID id) {
        try {
            log.info("[PDF Export] Starting PDF export for stock transaction: {}", id);
            
            // Get transaction to determine filename
            var result = stockTransactionAppService.getTransaction(id);
            if (result.getData() == null) {
                log.error("[PDF Export] Transaction not found: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            var transaction = result.getData();
            
            // Check if transactionType is null
            if (transaction.getTransactionType() == null) {
                log.error("[PDF Export] Transaction type is null for transaction: {}", id);
                return ResponseEntity.badRequest().build();
            }
            
            boolean isStockIn = transaction.getTransactionType() == 1;
            String type = isStockIn ? "NhapKho" : "XuatKho";
            String filename = String.format("Phieu_%s_%s.pdf", type, transaction.getTransactionCode());
            
            log.info("[PDF Export] Generating PDF for transaction: {} (type: {})", transaction.getTransactionCode(), type);
            
            // Generate PDF
            byte[] pdfBytes = pdfExportService.exportStockTransactionToPdf(id);
            
            log.info("[PDF Export] PDF generated successfully, size: {} bytes", pdfBytes.length);
            
            // Set headers for PDF download
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
                
        } catch (Exception e) {
            log.error("[PDF Export] Error exporting PDF for transaction: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
