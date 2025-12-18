package com.restaurant.ddd.application.model.inventorycount;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class InventoryCountRequest {
    private UUID warehouseId;
    private LocalDateTime countDate;
    private String notes;
    private List<InventoryCountItemRequest> items;
}
