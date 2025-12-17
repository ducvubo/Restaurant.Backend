package com.restaurant.ddd.application.model.ledger;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InventoryLedgerListRequest {
    private Integer page;
    private Integer size;
    private UUID warehouseId;
    private UUID materialId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
