package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.common.PageResponse;
import com.restaurant.ddd.application.model.purchasing.*;
import com.restaurant.ddd.application.service.PurchaseRequisitionAppService;
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
@RequestMapping("/api/management/purchase-requisitions")
@Tag(name = "Purchase Requisition", description = "APIs for managing purchase requisitions")
@Slf4j
public class PurchaseRequisitionController {

    @Autowired
    private PurchaseRequisitionAppService requisitionService;

    @Operation(summary = "Tạo yêu cầu mua hàng mới")
    @PostMapping("/create")
    public ResponseEntity<ResultMessage<PurchaseRequisitionDTO>> create(@RequestBody PurchaseRequisitionRequest request) {
        try {
            PurchaseRequisitionDTO result = requisitionService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResultUtil.data(result, "Tạo yêu cầu mua hàng thành công"));
        } catch (Exception e) {
            log.error("Error creating purchase requisition", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Lấy thông tin yêu cầu mua hàng theo ID")
    @GetMapping("/get")
    public ResponseEntity<ResultMessage<PurchaseRequisitionDTO>> getById(@RequestParam("id") UUID id) {
        try {
            PurchaseRequisitionDTO result = requisitionService.getById(id);
            return ResponseEntity.ok(ResultUtil.data(result, "Lấy thông tin thành công"));
        } catch (Exception e) {
            log.error("Error getting purchase requisition", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Lấy danh sách yêu cầu mua hàng")
    @GetMapping("/list")
    public ResultMessage<PageResponse<PurchaseRequisitionDTO>> getList(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortDir", required = false) String sortDir,
            @RequestParam(name = "keyword", required = false) String keyword,
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
        request.setWarehouseId(warehouseId);
        request.setStatus(status);
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        
        PageResponse<PurchaseRequisitionDTO> response = requisitionService.getList(request);
        return ResultUtil.data(response, "Lấy danh sách thành công");
    }

    @Operation(summary = "Cập nhật yêu cầu mua hàng")
    @PutMapping("/update")
    public ResponseEntity<ResultMessage<PurchaseRequisitionDTO>> update(
            @RequestParam("id") UUID id,
            @RequestBody PurchaseRequisitionRequest request) {
        try {
            PurchaseRequisitionDTO result = requisitionService.update(id, request);
            return ResponseEntity.ok(ResultUtil.data(result, "Cập nhật thành công"));
        } catch (Exception e) {
            log.error("Error updating purchase requisition", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Gửi phê duyệt yêu cầu mua hàng")
    @PostMapping("/submit")
    public ResponseEntity<ResultMessage<PurchaseRequisitionDTO>> submit(@RequestParam("id") UUID id) {
        try {
            PurchaseRequisitionDTO result = requisitionService.submit(id);
            return ResponseEntity.ok(ResultUtil.data(result, "Gửi phê duyệt thành công"));
        } catch (Exception e) {
            log.error("Error submitting purchase requisition", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Phê duyệt yêu cầu mua hàng")
    @PostMapping("/approve")
    public ResponseEntity<ResultMessage<PurchaseRequisitionDTO>> approve(@RequestParam("id") UUID id) {
        try {
            PurchaseRequisitionDTO result = requisitionService.approve(id);
            return ResponseEntity.ok(ResultUtil.data(result, "Phê duyệt thành công"));
        } catch (Exception e) {
            log.error("Error approving purchase requisition", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Từ chối yêu cầu mua hàng")
    @PostMapping("/reject")
    public ResponseEntity<ResultMessage<PurchaseRequisitionDTO>> reject(
            @RequestParam("id") UUID id,
            @RequestParam(name = "reason", required = false) String reason) {
        try {
            PurchaseRequisitionDTO result = requisitionService.reject(id, reason);
            return ResponseEntity.ok(ResultUtil.data(result, "Từ chối thành công"));
        } catch (Exception e) {
            log.error("Error rejecting purchase requisition", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Hủy yêu cầu mua hàng")
    @PostMapping("/cancel")
    public ResponseEntity<ResultMessage<PurchaseRequisitionDTO>> cancel(@RequestParam("id") UUID id) {
        try {
            PurchaseRequisitionDTO result = requisitionService.cancel(id);
            return ResponseEntity.ok(ResultUtil.data(result, "Hủy thành công"));
        } catch (Exception e) {
            log.error("Error cancelling purchase requisition", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Xóa yêu cầu mua hàng (chỉ trạng thái Nháp)")
    @DeleteMapping("/delete")
    public ResponseEntity<ResultMessage<Void>> delete(@RequestParam("id") UUID id) {
        try {
            requisitionService.delete(id);
            return ResponseEntity.ok(ResultUtil.data(null, "Xóa thành công"));
        } catch (Exception e) {
            log.error("Error deleting purchase requisition", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }
}
