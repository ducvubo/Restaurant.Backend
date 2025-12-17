package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.adjustment.AdjustmentListResponse;
import com.restaurant.ddd.application.model.adjustment.AdjustmentTransactionDTO;
import com.restaurant.ddd.application.model.adjustment.AdjustmentTransactionRequest;
import com.restaurant.ddd.application.model.ledger.LedgerPreviewResponse;
import com.restaurant.ddd.application.service.AdjustmentTransactionAppService;
import com.restaurant.ddd.application.service.PdfExportService;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import com.restaurant.ddd.domain.enums.AdjustmentType;
import com.restaurant.ddd.domain.enums.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/management/adjustments")
@RequiredArgsConstructor
@CrossOrigin
public class AdjustmentTransactionController {

    private final AdjustmentTransactionAppService adjustmentService;
    private final PdfExportService pdfExportService;

    @PostMapping
    public ResponseEntity<ResultMessage<AdjustmentTransactionDTO>> createAdjustment(@RequestBody AdjustmentTransactionRequest request) {
        var result = adjustmentService.createAdjustment(request);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @PutMapping
    public ResponseEntity<ResultMessage<AdjustmentTransactionDTO>> updateAdjustment(
            @RequestParam(name = "id") UUID id,
            @RequestBody AdjustmentTransactionRequest request) {
        var result = adjustmentService.updateAdjustment(id, request);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @GetMapping("/get")
    public ResponseEntity<ResultMessage<AdjustmentTransactionDTO>> getAdjustment(@RequestParam(name = "id") UUID id) {
        var result = adjustmentService.getAdjustment(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @GetMapping("/list")
    public ResponseEntity<ResultMessage<AdjustmentListResponse>> listAdjustments(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        var result = adjustmentService.listAdjustments(page, size);
        return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
    }

    @GetMapping("/preview-ledger")
    public ResponseEntity<ResultMessage<LedgerPreviewResponse>> previewLedger(
            @RequestParam(name = "id") UUID id) {
        var result = adjustmentService.previewLedger(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @PostMapping("/lock")
    public ResponseEntity<ResultMessage<Void>> lockAdjustment(@RequestParam(name = "id") UUID id) {
        var result = adjustmentService.lockAdjustment(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(null, result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestParam(name = "id") UUID id) {
        try {
            // Get adjustment to determine filename
            var result = adjustmentService.getAdjustment(id);
            if (result.getData() == null) {
                return ResponseEntity.notFound().build();
            }
            
            AdjustmentTransactionDTO adjustment = result.getData();
            String type = adjustment.getAdjustmentType() == AdjustmentType.INCREASE.code() ? "NhapKho" : "XuatKho";
            String filename = String.format("Phieu_%s_%s.pdf", type, adjustment.getTransactionCode());
            
            // Generate PDF
            byte[] pdfBytes = pdfExportService.exportAdjustmentToPdf(id);
            
            // Set headers for PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
