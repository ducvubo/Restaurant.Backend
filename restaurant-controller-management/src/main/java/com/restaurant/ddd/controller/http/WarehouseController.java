package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.warehouse.*;
import com.restaurant.ddd.application.service.WarehouseAppService;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import com.restaurant.ddd.domain.enums.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/management/warehouses")
@RequiredArgsConstructor
@Tag(name = "Warehouse Management", description = "APIs for managing warehouses")
@CrossOrigin
public class WarehouseController {

    private final WarehouseAppService warehouseAppService;

    @PostMapping("/create")
    @Operation(summary = "Create warehouse")
    public ResponseEntity<ResultMessage<WarehouseDTO>> create(@RequestBody CreateWarehouseRequest request) {
        var result = warehouseAppService.createWarehouse(request);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @PutMapping("/update")
    @Operation(summary = "Update warehouse")
    public ResponseEntity<ResultMessage<WarehouseDTO>> update(@RequestBody UpdateWarehouseRequest request) {
        var result = warehouseAppService.updateWarehouse(request);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @GetMapping("/get")
    @Operation(summary = "Get warehouse by ID")
    public ResponseEntity<ResultMessage<WarehouseDTO>> get(@RequestParam(name = "id") UUID id) {
        var result = warehouseAppService.getWarehouse(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @GetMapping("/list")
    @Operation(summary = "List warehouses")
    public ResponseEntity<ResultMessage<WarehouseListResponse>> list(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortDirection", required = false) String sortDirection,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "branchId", required = false) UUID branchId,
            @RequestParam(name = "warehouseType", required = false) Integer warehouseType
    ) {
        WarehouseListRequest request = new WarehouseListRequest();
        if (page != null) request.setPage(page);
        if (size != null) request.setSize(size);

        request.setSortBy(sortBy);
        request.setSortDirection(sortDirection);
        request.setKeyword(keyword);
        request.setStatus(status);
        request.setBranchId(branchId);
        request.setWarehouseType(warehouseType);
        
        var result = warehouseAppService.getList(request);
        return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
    }

    @PostMapping("/activate")
    @Operation(summary = "Activate warehouse")
    public ResponseEntity<ResultMessage<String>> activate(@RequestParam(name = "id") UUID id) {
        var result = warehouseAppService.activateWarehouse(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @PostMapping("/deactivate")
    @Operation(summary = "Deactivate warehouse")
    public ResponseEntity<ResultMessage<String>> deactivate(@RequestParam(name = "id") UUID id) {
        var result = warehouseAppService.deactivateWarehouse(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }
}
