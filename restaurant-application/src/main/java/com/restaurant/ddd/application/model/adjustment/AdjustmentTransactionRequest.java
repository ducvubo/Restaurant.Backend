package com.restaurant.ddd.application.model.adjustment;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AdjustmentTransactionRequest {
    private UUID warehouseId;
    private Integer adjustmentType;      // 1=Tăng, 2=Giảm
    private LocalDateTime transactionDate;
    private String reason;               // Bắt buộc
    private String referenceNumber;
    private String notes;
    private List<AdjustmentItemRequest> items;
}
