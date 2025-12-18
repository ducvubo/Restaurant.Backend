package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.inventorycount.*;
import com.restaurant.ddd.application.service.InventoryCountAppService;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Inventory Count", description = "Quản lý kiểm kê kho")
@RestController
@RequestMapping("/api/management/inventory-counts")
@RequiredArgsConstructor
@CrossOrigin
public class InventoryCountController {
    
    private final InventoryCountAppService inventoryCountAppService;
    
    @Operation(summary = "Tạo phiếu kiểm kê")
    @PostMapping
    public ResponseEntity<ResultMessage<InventoryCountDTO>> create(@RequestBody InventoryCountRequest request) {
        InventoryCountDTO result = inventoryCountAppService.create(request);
        return ResponseEntity.ok(ResultUtil.data(result, "Tạo phiếu kiểm kê thành công"));
    }
    
    @Operation(summary = "Cập nhật phiếu kiểm kê")
    @PutMapping
    public ResponseEntity<ResultMessage<InventoryCountDTO>> update(
            @RequestParam(name = "id") UUID id, 
            @RequestBody InventoryCountRequest request) {
        InventoryCountDTO result = inventoryCountAppService.update(id, request);
        return ResponseEntity.ok(ResultUtil.data(result, "Cập nhật phiếu kiểm kê thành công"));
    }
    
    @Operation(summary = "Lấy chi tiết phiếu kiểm kê")
    @GetMapping("/get")
    public ResponseEntity<ResultMessage<InventoryCountDTO>> get(@RequestParam(name = "id") UUID id) {
        InventoryCountDTO result = inventoryCountAppService.get(id);
        return ResponseEntity.ok(ResultUtil.data(result, "Lấy thông tin thành công"));
    }
    
    @Operation(summary = "Danh sách phiếu kiểm kê")
    @GetMapping("/list")
    public ResponseEntity<ResultMessage<InventoryCountListResponse>> list(InventoryCountListRequest request) {
        InventoryCountListResponse result = inventoryCountAppService.list(request);
        return ResponseEntity.ok(ResultUtil.data(result, "Lấy danh sách thành công"));
    }
    
    @Operation(summary = "Xóa phiếu kiểm kê")
    @DeleteMapping
    public ResponseEntity<ResultMessage<Void>> delete(@RequestParam(name = "id") UUID id) {
        inventoryCountAppService.delete(id);
        return ResponseEntity.ok(ResultUtil.data(null, "Xóa phiếu kiểm kê thành công"));
    }
    
    @Operation(summary = "Load danh sách lô hàng để kiểm kê")
    @GetMapping("/load-batches")
    public ResponseEntity<ResultMessage<List<BatchInfoDTO>>> loadBatches(@RequestParam(name = "warehouseId") UUID warehouseId) {
        List<BatchInfoDTO> result = inventoryCountAppService.loadBatchesForCount(warehouseId);
        return ResponseEntity.ok(ResultUtil.data(result, "Load danh sách lô thành công"));
    }
    
    @Operation(summary = "Hoàn thành kiểm kê và tạo phiếu điều chỉnh")
    @PostMapping("/complete")
    public ResponseEntity<ResultMessage<InventoryCountDTO>> complete(@RequestParam(name = "id") UUID id) {
        InventoryCountDTO result = inventoryCountAppService.complete(id);
        return ResponseEntity.ok(ResultUtil.data(result, "Hoàn thành kiểm kê thành công"));
    }
    
    @Operation(summary = "Hủy phiếu kiểm kê")
    @PostMapping("/cancel")
    public ResponseEntity<ResultMessage<InventoryCountDTO>> cancel(@RequestParam(name = "id") UUID id) {
        InventoryCountDTO result = inventoryCountAppService.cancel(id);
        return ResponseEntity.ok(ResultUtil.data(result, "Hủy phiếu kiểm kê thành công"));
    }
}

