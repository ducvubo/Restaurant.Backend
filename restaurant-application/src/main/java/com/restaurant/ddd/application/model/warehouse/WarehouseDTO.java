package com.restaurant.ddd.application.model.warehouse;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Warehouse
 */
@Data
public class WarehouseDTO {
    private UUID id;
    private String code;
    private String name;
    private UUID branchId;
    private String branchName; // For display
    private String address;
    private BigDecimal capacity;
    private UUID managerId;
    private String managerName; // For display
    private Integer warehouseType; // Code
    private String warehouseTypeName; // Display
    private Integer status; // Code
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
