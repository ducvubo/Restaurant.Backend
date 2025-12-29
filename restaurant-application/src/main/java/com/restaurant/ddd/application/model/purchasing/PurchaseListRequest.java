package com.restaurant.ddd.application.model.purchasing;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * List request for purchasing module with pagination and filters
 */
@Data
public class PurchaseListRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
    private UUID warehouseId;
    private UUID supplierId;
    private Integer status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String sortBy = "createdDate";
    private String sortDir = "desc";
}
