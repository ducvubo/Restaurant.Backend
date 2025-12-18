package com.restaurant.ddd.application.model.inventorycount;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class InventoryCountDTO {
    private UUID id;
    private String countCode;
    private UUID warehouseId;
    private String warehouseName;
    private LocalDateTime countDate;
    private Integer countStatus;
    private String countStatusName;
    private String notes;
    private UUID adjustmentTransactionId;
    private String adjustmentTransactionCode;
    private UUID performedBy;
    private String performedByName;
    private Integer status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<InventoryCountItemDTO> items;
}
