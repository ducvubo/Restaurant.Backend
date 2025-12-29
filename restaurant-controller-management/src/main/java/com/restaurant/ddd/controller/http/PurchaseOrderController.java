package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.common.PageResponse;
import com.restaurant.ddd.application.model.purchasing.*;
import com.restaurant.ddd.application.service.PurchaseOrderAppService;
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
@RequestMapping("/api/management/purchase-orders")
@Tag(name = "Purchase Order", description = "APIs for managing purchase orders")
@Slf4j
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderAppService poService;

    @Operation(summary = "Tạo đơn đặt hàng mới")
    @PostMapping("/create")
    public ResponseEntity<ResultMessage<PurchaseOrderDTO>> create(@RequestBody PurchaseOrderRequest request) {
        try {
            PurchaseOrderDTO result = poService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResultUtil.data(result, "Tạo đơn đặt hàng thành công"));
        } catch (Exception e) {
            log.error("Error creating purchase order", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Tạo PO từ báo giá đã chấp nhận")
    @PostMapping("/create-from-rfq")
    public ResponseEntity<ResultMessage<PurchaseOrderDTO>> createFromRfq(@RequestParam("rfqId") UUID rfqId) {
        try {
            PurchaseOrderDTO result = poService.createFromRfq(rfqId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResultUtil.data(result, "Tạo đơn đặt hàng từ RFQ thành công"));
        } catch (Exception e) {
            log.error("Error creating PO from RFQ", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Lấy thông tin đơn đặt hàng theo ID")
    @GetMapping("/get")
    public ResponseEntity<ResultMessage<PurchaseOrderDTO>> getById(@RequestParam("id") UUID id) {
        try {
            PurchaseOrderDTO result = poService.getById(id);
            return ResponseEntity.ok(ResultUtil.data(result, "Lấy thông tin thành công"));
        } catch (Exception e) {
            log.error("Error getting purchase order", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Lấy danh sách đơn đặt hàng")
    @GetMapping("/list")
    public ResultMessage<PageResponse<PurchaseOrderDTO>> getList(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortDir", required = false) String sortDir,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "supplierId", required = false) UUID supplierId,
            @RequestParam(name = "warehouseId", required = false) UUID warehouseId,
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
        request.setWarehouseId(warehouseId);
        request.setStatus(status);
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        
        PageResponse<PurchaseOrderDTO> response = poService.getList(request);
        return ResultUtil.data(response, "Lấy danh sách thành công");
    }

    @Operation(summary = "Cập nhật đơn đặt hàng")
    @PutMapping("/update")
    public ResponseEntity<ResultMessage<PurchaseOrderDTO>> update(
            @RequestParam("id") UUID id,
            @RequestBody PurchaseOrderRequest request) {
        try {
            PurchaseOrderDTO result = poService.update(id, request);
            return ResponseEntity.ok(ResultUtil.data(result, "Cập nhật thành công"));
        } catch (Exception e) {
            log.error("Error updating purchase order", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Xác nhận đơn đặt hàng với nhà cung cấp")
    @PostMapping("/confirm")
    public ResponseEntity<ResultMessage<PurchaseOrderDTO>> confirm(@RequestParam("id") UUID id) {
        try {
            PurchaseOrderDTO result = poService.confirm(id);
            return ResponseEntity.ok(ResultUtil.data(result, "Xác nhận đơn hàng thành công"));
        } catch (Exception e) {
            log.error("Error confirming purchase order", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Nhận hàng (tạo phiếu nhập kho)")
    @PostMapping("/receive-goods")
    public ResponseEntity<ResultMessage<PurchaseOrderDTO>> receiveGoods(
            @RequestParam("id") UUID id,
            @RequestBody ReceiveGoodsRequest request) {
        try {
            PurchaseOrderDTO result = poService.receiveGoods(id, request);
            return ResponseEntity.ok(ResultUtil.data(result, "Nhận hàng thành công"));
        } catch (Exception e) {
            log.error("Error receiving goods", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Hủy đơn đặt hàng")
    @PostMapping("/cancel")
    public ResponseEntity<ResultMessage<PurchaseOrderDTO>> cancel(@RequestParam("id") UUID id) {
        try {
            PurchaseOrderDTO result = poService.cancel(id);
            return ResponseEntity.ok(ResultUtil.data(result, "Hủy đơn hàng thành công"));
        } catch (Exception e) {
            log.error("Error cancelling purchase order", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Xóa đơn đặt hàng (chỉ trạng thái Nháp)")
    @DeleteMapping("/delete")
    public ResponseEntity<ResultMessage<Void>> delete(@RequestParam("id") UUID id) {
        try {
            poService.delete(id);
            return ResponseEntity.ok(ResultUtil.data(null, "Xóa thành công"));
        } catch (Exception e) {
            log.error("Error deleting purchase order", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }
}
