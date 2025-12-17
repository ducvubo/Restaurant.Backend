package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.supplier.*;
import com.restaurant.ddd.application.service.SupplierAppService;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import com.restaurant.ddd.domain.enums.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/management/suppliers")
@Tag(name = "Supplier Management", description = "APIs for managing suppliers")
@Slf4j
public class SupplierController {

    @Autowired
    private SupplierAppService supplierAppService;

    @Operation(summary = "Tạo nhà cung cấp mới")
    @ApiResponse(responseCode = "201", description = "Tạo nhà cung cấp thành công")
    @PostMapping("/create")
    public ResponseEntity<ResultMessage<SupplierDTO>> createSupplier(@RequestBody CreateSupplierRequest request) {
        try {
            SupplierDTO supplier = supplierAppService.createSupplier(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResultUtil.data(supplier, "Tạo nhà cung cấp thành công"));
        } catch (Exception e) {
            log.error("Error creating supplier", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Lấy thông tin nhà cung cấp theo ID")
    @GetMapping("/get")
    public ResponseEntity<ResultMessage<SupplierDTO>> getSupplier(@RequestParam("id") UUID id) {
        try {
            SupplierDTO supplier = supplierAppService.getSupplierById(id);
            return ResponseEntity.ok(ResultUtil.data(supplier, "Lấy thông tin nhà cung cấp thành công"));
        } catch (Exception e) {
            log.error("Error getting supplier", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Lấy danh sách tất cả nhà cung cấp")
    @GetMapping("/get-all")
    public ResultMessage<List<SupplierDTO>> getAllSuppliers() {
        List<SupplierDTO> suppliers = supplierAppService.getAllSuppliers();
        return ResultUtil.data(suppliers, ResultCode.SUCCESS);
    }

    @Operation(summary = "Lấy danh sách nhà cung cấp với phân trang")
    @GetMapping("/list")
    public ResultMessage<SupplierListResponse> getList(@ModelAttribute SupplierListRequest request) {
        SupplierListResponse response = supplierAppService.getList(request);
        return ResultUtil.data(response, "Lấy danh sách nhà cung cấp thành công");
    }

    @Operation(summary = "Cập nhật nhà cung cấp")
    @PutMapping("/update")
    public ResponseEntity<ResultMessage<SupplierDTO>> updateSupplier(
            @RequestParam("id") UUID id,
            @RequestBody UpdateSupplierRequest request) {
        try {
            request.setId(id);
            SupplierDTO supplier = supplierAppService.updateSupplier(id, request);
            return ResponseEntity.ok(ResultUtil.data(supplier, "Cập nhật nhà cung cấp thành công"));
        } catch (Exception e) {
            log.error("Error updating supplier", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Kích hoạt nhà cung cấp")
    @PutMapping("/activate")
    public ResponseEntity<ResultMessage<SupplierDTO>> activateSupplier(@RequestParam("id") UUID id) {
        try {
            SupplierDTO supplier = supplierAppService.activateSupplier(id);
            return ResponseEntity.ok(ResultUtil.data(supplier, "Kích hoạt nhà cung cấp thành công"));
        } catch (Exception e) {
            log.error("Error activating supplier", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Vô hiệu hóa nhà cung cấp")
    @PutMapping("/deactivate")
    public ResponseEntity<ResultMessage<SupplierDTO>> deactivateSupplier(@RequestParam("id") UUID id) {
        try {
            SupplierDTO supplier = supplierAppService.deactivateSupplier(id);
            return ResponseEntity.ok(ResultUtil.data(supplier, "Vô hiệu hóa nhà cung cấp thành công"));
        } catch (Exception e) {
            log.error("Error deactivating supplier", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }
}
