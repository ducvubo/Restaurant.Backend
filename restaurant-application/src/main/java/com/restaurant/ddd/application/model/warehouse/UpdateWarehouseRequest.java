package com.restaurant.ddd.application.model.warehouse;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request for updating Warehouse
 */
@Data
public class UpdateWarehouseRequest {
    private UUID id;
    private String code;
    private String name;
    private UUID branchId;
    private String address;
    private BigDecimal capacity;
    private UUID managerId;
    private Integer warehouseType;
}
