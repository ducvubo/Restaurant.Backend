package com.restaurant.ddd.application.model.ledger;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LedgerPreviewBatchDTO {
    private UUID batchId;
    private String batchCode;
    private String batchNumber;  // Batch number/code for display
    private LocalDateTime transactionDate;
    private BigDecimal quantityUsed;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private BigDecimal remainingAfter;
}
