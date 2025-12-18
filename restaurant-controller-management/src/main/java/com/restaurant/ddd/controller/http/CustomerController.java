package com.restaurant.ddd.controller.http;

import com.restaurant.ddd.application.model.customer.CreateCustomerRequest;
import com.restaurant.ddd.application.model.customer.CustomerDTO;
import com.restaurant.ddd.application.model.customer.CustomerListResponse;
import com.restaurant.ddd.application.model.customer.UpdateCustomerRequest;
import com.restaurant.ddd.application.service.CustomerAppService;
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
@RequestMapping("/api/management/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing customers")
@CrossOrigin
public class CustomerController {

    private final CustomerAppService customerAppService;

    @PostMapping("/create")
    @Operation(summary = "Create customer")
    public ResponseEntity<ResultMessage<CustomerDTO>> create(@RequestBody CreateCustomerRequest request) {
        var result = customerAppService.createCustomer(request);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @PutMapping("/update")
    @Operation(summary = "Update customer")
    public ResponseEntity<ResultMessage<CustomerDTO>> update(@RequestBody UpdateCustomerRequest request) {
        var result = customerAppService.updateCustomer(request);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @GetMapping("/get")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<ResultMessage<CustomerDTO>> get(@RequestParam(name = "id") UUID id) {
        var result = customerAppService.getCustomer(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @GetMapping("/list")
    @Operation(summary = "List all customers")
    public ResponseEntity<ResultMessage<CustomerListResponse>> list(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortDirection", required = false) String sortDirection,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) Integer status
    ) {
        com.restaurant.ddd.application.model.customer.CustomerListRequest request = 
            new com.restaurant.ddd.application.model.customer.CustomerListRequest();
        if (page != null) request.setPage(page);
        if (size != null) request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDirection(sortDirection);
        request.setKeyword(keyword);
        request.setStatus(status);
        
        var result = customerAppService.getList(request);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @PutMapping("/deactivate")
    @Operation(summary = "Deactivate customer")
    public ResponseEntity<ResultMessage<String>> deactivate(@RequestParam(name = "id") UUID id) {
        var result = customerAppService.deactivateCustomer(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }

    @PutMapping("/activate")
    @Operation(summary = "Activate customer")
    public ResponseEntity<ResultMessage<String>> activate(@RequestParam(name = "id") UUID id) {
        var result = customerAppService.activateCustomer(id);
        if (result.getCode() == ResultCode.SUCCESS) {
            return ResponseEntity.ok(ResultUtil.data(result.getData(), result.getMessage()));
        }
        return ResponseEntity.ok(ResultUtil.error(ResultCode.ERROR.code(), result.getMessage()));
    }
}
