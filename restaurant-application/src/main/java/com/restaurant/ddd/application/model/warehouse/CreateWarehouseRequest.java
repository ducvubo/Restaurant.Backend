package com.restaurant.ddd.application.model.warehouse;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request for creating Warehouse
 */
@Data
public class CreateWarehouseRequest {
    private String code;
    private String name;
    private UUID branchId;
    private String address;
    private BigDecimal capacity;
    private UUID managerId;
    private Integer warehouseType;
}
