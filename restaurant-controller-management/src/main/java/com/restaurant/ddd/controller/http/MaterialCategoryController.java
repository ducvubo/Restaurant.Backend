package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.material.CreateMaterialCategoryRequest;
import com.restaurant.ddd.application.model.material.MaterialCategoryDTO;
import com.restaurant.ddd.application.model.material.MaterialCategoryListRequest;
import com.restaurant.ddd.application.model.material.MaterialCategoryListResponse;
import com.restaurant.ddd.application.model.material.UpdateMaterialCategoryRequest;
import com.restaurant.ddd.application.service.MaterialCategoryService;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/management/material-categories")
@RequiredArgsConstructor
@Tag(name = "Material Category Management", description = "APIs for managing material categories")
@CrossOrigin
public class MaterialCategoryController {

    private final MaterialCategoryService service;

    @PostMapping("/create")
    @Operation(summary = "Create material category")
    public ResponseEntity<ResultMessage<MaterialCategoryDTO>> create(@RequestBody CreateMaterialCategoryRequest request) {
        var result = service.create(request);
        return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
    }

    @PostMapping("/update")
    @Operation(summary = "Update material category")
    public ResponseEntity<ResultMessage<MaterialCategoryDTO>> update(@RequestBody UpdateMaterialCategoryRequest request) {
        var result = service.update(request);
        return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
    }

    @GetMapping("/get")
    @Operation(summary = "Get material category by ID")
    public ResponseEntity<ResultMessage<MaterialCategoryDTO>> get(@RequestParam(name = "id") UUID id) {
        var result = service.getById(id);
        return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
    }

    @GetMapping("/list")
    @Operation(summary = "List material categories with pagination")
    public ResponseEntity<ResultMessage<MaterialCategoryListResponse>> list(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        MaterialCategoryListRequest request = new MaterialCategoryListRequest();
        request.setPage(page);
        request.setSize(size);
        request.setKeyword(keyword);
        var result = service.getList(request);
        return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
    }

    @PostMapping("/delete")
    @Operation(summary = "Delete material category")
    public ResponseEntity<ResultMessage<String>> delete(@RequestParam(name = "id") UUID id) {
        var result = service.delete(id);
        return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
    }

    @PostMapping("/activate")
    @Operation(summary = "Activate material category")
    public ResponseEntity<ResultMessage<String>> activate(@RequestParam(name = "id") UUID id) {
        var result = service.activate(id);
        return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
    }

    @PostMapping("/deactivate")
    @Operation(summary = "Deactivate material category")
    public ResponseEntity<ResultMessage<String>> deactivate(@RequestParam(name = "id") UUID id) {
        var result = service.deactivate(id);
        return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
    }
}
