package com.restaurant.ddd.application.model.warehouse;

import com.restaurant.ddd.application.model.common.PageResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response for listing warehouses
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarehouseListResponse extends PageResponse<WarehouseDTO> {
    // Can add specific fields if needed
}
