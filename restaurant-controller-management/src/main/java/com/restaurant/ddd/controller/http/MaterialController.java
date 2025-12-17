package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.material.*;
import com.restaurant.ddd.application.service.MaterialAppService;
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
@RequestMapping("/api/management/materials")
@RequiredArgsConstructor
@Tag(name = "Material Management", description = "APIs for managing materials")
@CrossOrigin
public class MaterialController {

    private final MaterialAppService materialAppService;

    @PostMapping("/create")
    @Operation(summary = "Create material")
    public ResponseEntity<ResultMessage<MaterialDTO>> create(@RequestBody CreateMaterialRequest request) {
        var result = materialAppService.createMaterial(request);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @PutMapping("/update")
    @Operation(summary = "Update material")
    public ResponseEntity<ResultMessage<MaterialDTO>> update(@RequestBody UpdateMaterialRequest request) {
        var result = materialAppService.updateMaterial(request);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @GetMapping("/get")
    @Operation(summary = "Get material by ID")
    public ResponseEntity<ResultMessage<MaterialDTO>> get(@RequestParam(name = "id") UUID id) {
        var result = materialAppService.getMaterial(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @GetMapping("/list")
    @Operation(summary = "List materials")
    public ResponseEntity<ResultMessage<MaterialListResponse>> list(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "category", required = false) String category
    ) {
        MaterialListRequest request = new MaterialListRequest();
        request.setPage(page);
        request.setSize(size);
        request.setKeyword(keyword);
        request.setStatus(status);
        request.setCategory(category);
        
        var result = materialAppService.getList(request);
        return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
    }

    @PostMapping("/activate")
    @Operation(summary = "Activate material")
    public ResponseEntity<ResultMessage<String>> activate(@RequestParam(name = "id") UUID id) {
        var result = materialAppService.activateMaterial(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @PostMapping("/deactivate")
    @Operation(summary = "Deactivate material")
    public ResponseEntity<ResultMessage<String>> deactivate(@RequestParam(name = "id") UUID id) {
        var result = materialAppService.deactivateMaterial(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }
}
