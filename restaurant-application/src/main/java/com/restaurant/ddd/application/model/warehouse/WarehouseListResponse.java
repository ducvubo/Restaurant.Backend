package com.restaurant.ddd.application.model.warehouse;

import lombok.Data;

import java.util.List;

/**
 * Response for listing warehouses
 */
@Data
public class WarehouseListResponse {
    private List<WarehouseDTO> items;
    private long total;
    private int page;
    private int size;
    private int totalPages;
}
