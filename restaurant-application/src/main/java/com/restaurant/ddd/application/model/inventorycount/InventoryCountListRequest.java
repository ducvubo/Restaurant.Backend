package com.restaurant.ddd.application.model.inventorycount;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InventoryCountListRequest {
    private Integer page = 1;
    private Integer size = 10;
    private UUID warehouseId;
    private Integer countStatus;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Integer status;
}
