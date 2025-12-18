package com.restaurant.ddd.application.model.inventorycount;

import lombok.Data;

import java.util.List;

@Data
public class InventoryCountListResponse {
    private List<InventoryCountDTO> items;
    private Long total;
    private Integer page;
    private Integer size;
}
