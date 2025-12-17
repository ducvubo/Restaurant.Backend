package com.restaurant.ddd.application.model.warehouse;

import lombok.Data;

import java.util.UUID;

/**
 * Request for listing warehouses
 */
@Data
public class WarehouseListRequest {
    private Integer page;
    private Integer size;
    private String keyword;
    private Integer status;
    private UUID branchId;
    private Integer warehouseType;
}
