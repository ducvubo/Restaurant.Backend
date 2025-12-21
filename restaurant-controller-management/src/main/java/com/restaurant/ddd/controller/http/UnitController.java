package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.unit.*;
import com.restaurant.ddd.application.service.UnitAppService;
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
@RequestMapping("/api/management/units")
@Tag(name = "Unit Management", description = "APIs for managing measurement units")
@Slf4j
public class UnitController {

    @Autowired
    private UnitAppService unitAppService;

    @Operation(summary = "Tạo đơn vị tính mới")
    @ApiResponse(responseCode = "201", description = "Tạo đơn vị tính thành công")
    @PostMapping("/create")
    public ResponseEntity<ResultMessage<UnitDTO>> createUnit(@RequestBody CreateUnitRequest request) {
        try {
            UnitDTO unit = unitAppService.createUnit(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResultUtil.data(unit, "Tạo đơn vị tính thành công"));
        } catch (Exception e) {
            log.error("Error creating unit", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Lấy thông tin đơn vị tính theo ID")
    @GetMapping("/get")
    public ResponseEntity<ResultMessage<UnitDTO>> getUnit(@RequestParam("id") UUID id) {
        try {
            UnitDTO unit = unitAppService.getUnitById(id);
            return ResponseEntity.ok(ResultUtil.data(unit, "Lấy thông tin đơn vị tính thành công"));
        } catch (Exception e) {
            log.error("Error getting unit", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Lấy danh sách tất cả đơn vị tính")
    @GetMapping("/get-all")
    public ResultMessage<List<UnitDTO>> getAllUnits() {
        List<UnitDTO> units = unitAppService.getAllUnits();
        return ResultUtil.data(units, ResultCode.SUCCESS);
    }

    @Operation(summary = "Lấy danh sách đơn vị tính với phân trang")
    @GetMapping("/list")
    public ResultMessage<UnitListResponseNew> getList(@ModelAttribute UnitListRequestNew request) {
        UnitListResponseNew response = unitAppService.getList(request);
        return ResultUtil.data(response, "Lấy danh sách đơn vị tính thành công");
    }

    @Operation(summary = "Cập nhật đơn vị tính")
    @PutMapping("/update")
    public ResponseEntity<ResultMessage<UnitDTO>> updateUnit(
            @RequestParam("id") UUID id,
            @RequestBody UpdateUnitRequest request) {
        try {
            request.setId(id);
            UnitDTO unit = unitAppService.updateUnit(id, request);
            return ResponseEntity.ok(ResultUtil.data(unit, "Cập nhật đơn vị tính thành công"));
        } catch (Exception e) {
            log.error("Error updating unit", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Kích hoạt đơn vị tính")
    @PutMapping("/activate")
    public ResponseEntity<ResultMessage<UnitDTO>> activateUnit(@RequestParam("id") UUID id) {
        try {
            UnitDTO unit = unitAppService.activateUnit(id);
            return ResponseEntity.ok(ResultUtil.data(unit, "Kích hoạt đơn vị tính thành công"));
        } catch (Exception e) {
            log.error("Error activating unit", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Vô hiệu hóa đơn vị tính")
    @PutMapping("/deactivate")
    public ResponseEntity<ResultMessage<UnitDTO>> deactivateUnit(@RequestParam("id") UUID id) {
        try {
            UnitDTO unit = unitAppService.deactivateUnit(id);
            return ResponseEntity.ok(ResultUtil.data(unit, "Vô hiệu hóa đơn vị tính thành công"));
        } catch (Exception e) {
            log.error("Error deactivating unit", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }
}
