package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.common.PageResponse;
import com.restaurant.ddd.application.model.purchasing.*;
import com.restaurant.ddd.application.service.RfqAppService;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import com.restaurant.ddd.domain.enums.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/management/rfqs")
@Tag(name = "Request For Quotation", description = "APIs for managing RFQs")
@Slf4j
public class RfqController {

    @Autowired
    private RfqAppService rfqService;

    @Operation(summary = "Tạo yêu cầu báo giá mới")
    @PostMapping("/create")
    public ResponseEntity<ResultMessage<RfqDTO>> create(@RequestBody RfqRequest request) {
        try {
            RfqDTO result = rfqService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResultUtil.data(result, "Tạo yêu cầu báo giá thành công"));
        } catch (Exception e) {
            log.error("Error creating RFQ", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Tạo RFQ từ yêu cầu mua hàng đã duyệt")
    @PostMapping("/create-from-requisition")
    public ResponseEntity<ResultMessage<RfqDTO>> createFromRequisition(
            @RequestParam("requisitionId") UUID requisitionId,
            @RequestParam("supplierId") UUID supplierId) {
        try {
            RfqDTO result = rfqService.createFromRequisition(requisitionId, supplierId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResultUtil.data(result, "Tạo RFQ từ yêu cầu mua hàng thành công"));
        } catch (Exception e) {
            log.error("Error creating RFQ from requisition", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Lấy thông tin RFQ theo ID")
    @GetMapping("/get")
    public ResponseEntity<ResultMessage<RfqDTO>> getById(@RequestParam("id") UUID id) {
        try {
            RfqDTO result = rfqService.getById(id);
            return ResponseEntity.ok(ResultUtil.data(result, "Lấy thông tin thành công"));
        } catch (Exception e) {
            log.error("Error getting RFQ", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Lấy danh sách RFQ")
    @GetMapping("/list")
    public ResultMessage<PageResponse<RfqDTO>> getList(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortDir", required = false) String sortDir,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "supplierId", required = false) UUID supplierId,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "fromDate", required = false) LocalDateTime fromDate,
            @RequestParam(name = "toDate", required = false) LocalDateTime toDate
    ) {
        PurchaseListRequest request = new PurchaseListRequest();
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDir(sortDir);
        request.setKeyword(keyword);
        request.setSupplierId(supplierId);
        request.setStatus(status);
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        
        PageResponse<RfqDTO> response = rfqService.getList(request);
        return ResultUtil.data(response, "Lấy danh sách thành công");
    }

    @Operation(summary = "Cập nhật RFQ")
    @PutMapping("/update")
    public ResponseEntity<ResultMessage<RfqDTO>> update(
            @RequestParam("id") UUID id,
            @RequestBody RfqRequest request) {
        try {
            RfqDTO result = rfqService.update(id, request);
            return ResponseEntity.ok(ResultUtil.data(result, "Cập nhật thành công"));
        } catch (Exception e) {
            log.error("Error updating RFQ", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Gửi RFQ cho nhà cung cấp")
    @PostMapping("/send")
    public ResponseEntity<ResultMessage<RfqDTO>> send(@RequestParam("id") UUID id) {
        try {
            RfqDTO result = rfqService.send(id);
            return ResponseEntity.ok(ResultUtil.data(result, "Gửi RFQ thành công"));
        } catch (Exception e) {
            log.error("Error sending RFQ", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Nhận báo giá từ nhà cung cấp")
    @PostMapping("/receive-quotation")
    public ResponseEntity<ResultMessage<RfqDTO>> receiveQuotation(
            @RequestParam("id") UUID id,
            @RequestBody RfqRequest quotation) {
        try {
            RfqDTO result = rfqService.receiveQuotation(id, quotation);
            return ResponseEntity.ok(ResultUtil.data(result, "Nhận báo giá thành công"));
        } catch (Exception e) {
            log.error("Error receiving quotation", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Chấp nhận báo giá")
    @PostMapping("/accept")
    public ResponseEntity<ResultMessage<RfqDTO>> accept(@RequestParam("id") UUID id) {
        try {
            RfqDTO result = rfqService.accept(id);
            return ResponseEntity.ok(ResultUtil.data(result, "Chấp nhận báo giá thành công"));
        } catch (Exception e) {
            log.error("Error accepting RFQ", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Từ chối báo giá")
    @PostMapping("/reject")
    public ResponseEntity<ResultMessage<RfqDTO>> reject(@RequestParam("id") UUID id) {
        try {
            RfqDTO result = rfqService.reject(id);
            return ResponseEntity.ok(ResultUtil.data(result, "Từ chối báo giá thành công"));
        } catch (Exception e) {
            log.error("Error rejecting RFQ", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Hủy RFQ")
    @PostMapping("/cancel")
    public ResponseEntity<ResultMessage<RfqDTO>> cancel(@RequestParam("id") UUID id) {
        try {
            RfqDTO result = rfqService.cancel(id);
            return ResponseEntity.ok(ResultUtil.data(result, "Hủy RFQ thành công"));
        } catch (Exception e) {
            log.error("Error cancelling RFQ", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Xóa RFQ (chỉ trạng thái Nháp)")
    @DeleteMapping("/delete")
    public ResponseEntity<ResultMessage<Void>> delete(@RequestParam("id") UUID id) {
        try {
            rfqService.delete(id);
            return ResponseEntity.ok(ResultUtil.data(null, "Xóa thành công"));
        } catch (Exception e) {
            log.error("Error deleting RFQ", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }
}
