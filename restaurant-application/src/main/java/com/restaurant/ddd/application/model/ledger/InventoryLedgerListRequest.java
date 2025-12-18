package com.restaurant.ddd.application.model.ledger;

import com.restaurant.ddd.application.model.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class InventoryLedgerListRequest extends PageRequest {
    private UUID warehouseId;
    private UUID materialId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
