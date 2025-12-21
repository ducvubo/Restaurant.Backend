package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.unitconversion.MaterialUnitDTO;
import com.restaurant.ddd.application.model.unitconversion.UnitConversionDTO;
import com.restaurant.ddd.application.model.unitconversion.UnitConversionListRequest;
import com.restaurant.ddd.application.model.unitconversion.UnitConversionListResponse;
import com.restaurant.ddd.application.model.unitconversion.UnitConversionRequest;
import com.restaurant.ddd.application.service.UnitConversionService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/management/unit-conversions")
@Tag(name = "Unit Conversion Management", description = "APIs for managing unit conversions")
@Slf4j
public class UnitConversionController {

    @Autowired
    private UnitConversionService unitConversionService;

    @Operation(summary = "Lấy danh sách hệ số chuyển đổi với phân trang")
    @GetMapping("/list")
    public ResultMessage<UnitConversionListResponse> list(@ModelAttribute UnitConversionListRequest request) {
        UnitConversionListResponse response = unitConversionService.getList(request);
        return ResultUtil.data(response, "Lấy danh sách hệ số chuyển đổi thành công");
    }

    @Operation(summary = "Tạo hệ số chuyển đổi mới")
    @ApiResponse(responseCode = "201", description = "Tạo hệ số chuyển đổi thành công")
    @PostMapping("/create")
    public ResponseEntity<ResultMessage<UnitConversionDTO>> create(@RequestBody UnitConversionRequest request) {
        try {
            com.restaurant.ddd.domain.model.ResultMessage<UnitConversionDTO> result = unitConversionService.createConversion(request);
            if (result.getCode() == ResultCode.SUCCESS) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ResultUtil.data(result.getData(), "Tạo hệ số chuyển đổi thành công"));
            } else {
                return ResponseEntity.ok(ResultUtil.error(result.getCode().code(), result.getMessage()));
            }
        } catch (Exception e) {
            log.error("Error creating conversion", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Cập nhật hệ số chuyển đổi")
    @PutMapping("/update")
    public ResponseEntity<ResultMessage<UnitConversionDTO>> update(
            @RequestParam("id") UUID id,
            @RequestBody UnitConversionRequest request) {
        try {
            com.restaurant.ddd.domain.model.ResultMessage<UnitConversionDTO> result = unitConversionService.updateConversion(id, request);
            if (result.getCode() == ResultCode.SUCCESS) {
                return ResponseEntity.ok(ResultUtil.data(result.getData(), "Cập nhật hệ số chuyển đổi thành công"));
            } else {
                return ResponseEntity.ok(ResultUtil.error(result.getCode().code(), result.getMessage()));
            }
        } catch (Exception e) {
            log.error("Error updating conversion", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Xóa hệ số chuyển đổi")
    @DeleteMapping("/delete")
    public ResponseEntity<ResultMessage<Void>> delete(@RequestParam("id") UUID id) {
        try {
            com.restaurant.ddd.domain.model.ResultMessage<Void> result = unitConversionService.deleteConversion(id);
            return ResponseEntity.ok(ResultUtil.data(null, result.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting conversion", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Chuyển đổi số lượng giữa 2 đơn vị")
    @GetMapping("/convert")
    public ResponseEntity<ResultMessage<BigDecimal>> convert(
            @RequestParam BigDecimal quantity,
            @RequestParam UUID fromUnitId,
            @RequestParam UUID toUnitId) {
        try {
            BigDecimal result = unitConversionService.convertQuantity(quantity, fromUnitId, toUnitId);
            return ResponseEntity.ok(ResultUtil.data(result, "Chuyển đổi thành công"));
        } catch (Exception e) {
            log.error("Error converting quantity", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Lấy danh sách đơn vị cho phép của nguyên liệu")
    @GetMapping("/materials/units")
    public ResponseEntity<ResultMessage<List<MaterialUnitDTO>>> getUnitsForMaterial(
            @RequestParam("materialId") UUID materialId) {
        try {
            List<MaterialUnitDTO> units = unitConversionService.getUnitsForMaterial(materialId);
            return ResponseEntity.ok(ResultUtil.data(units, "Lấy danh sách đơn vị thành công"));
        } catch (Exception e) {
            log.error("Error getting units for material", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Thêm đơn vị cho nguyên liệu")
    @PostMapping("/materials/units/add")
    public ResponseEntity<ResultMessage<MaterialUnitDTO>> addUnitToMaterial(
            @RequestParam("materialId") UUID materialId,
            @RequestParam("unitId") UUID unitId,
            @RequestParam(value = "isBaseUnit", required = false, defaultValue = "false") Boolean isBaseUnit) {
        try {
            com.restaurant.ddd.domain.model.ResultMessage<MaterialUnitDTO> result = unitConversionService.addUnitToMaterial(materialId, unitId, isBaseUnit);
            if (result.getCode() == ResultCode.SUCCESS) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ResultUtil.data(result.getData(), "Thêm đơn vị cho nguyên liệu thành công"));
            } else {
                return ResponseEntity.ok(ResultUtil.error(result.getCode().code(), result.getMessage()));
            }
        } catch (Exception e) {
            log.error("Error adding unit to material", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Xóa đơn vị khỏi nguyên liệu")
    @DeleteMapping("/materials/units/remove")
    public ResponseEntity<ResultMessage<Void>> removeUnitFromMaterial(
            @RequestParam("materialId") UUID materialId,
            @RequestParam("unitId") UUID unitId) {
        try {
            com.restaurant.ddd.domain.model.ResultMessage<Void> result = unitConversionService.removeUnitFromMaterial(materialId, unitId);
            if (result.getCode() == com.restaurant.ddd.domain.enums.ResultCode.ERROR) {
                return ResponseEntity.badRequest().body(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
            }
            return ResponseEntity.ok(ResultUtil.data(null, result.getMessage()));
        } catch (Exception e) {
            log.error("Error removing unit from material", e);
            return ResponseEntity.badRequest().body(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }

    @Operation(summary = "Đặt đơn vị cơ sở cho nguyên liệu")
    @PutMapping("/materials/units/set-base")
    public ResponseEntity<ResultMessage<Void>> setBaseUnit(
            @RequestParam("materialId") UUID materialId,
            @RequestParam("unitId") UUID unitId) {
        try {
            com.restaurant.ddd.domain.model.ResultMessage<Void> result = unitConversionService.setBaseUnit(materialId, unitId);
            return ResponseEntity.ok(ResultUtil.data(null, result.getMessage()));
        } catch (Exception e) {
            log.error("Error setting base unit", e);
            return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), e.getMessage()));
        }
    }
}
