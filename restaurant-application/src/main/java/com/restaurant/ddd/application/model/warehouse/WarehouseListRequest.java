package com.restaurant.ddd.application.model.warehouse;

import com.restaurant.ddd.application.model.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * Request for listing warehouses
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarehouseListRequest extends PageRequest {
    private String keyword;
    private Integer status;
    private UUID branchId;
    private Integer warehouseType;
}
