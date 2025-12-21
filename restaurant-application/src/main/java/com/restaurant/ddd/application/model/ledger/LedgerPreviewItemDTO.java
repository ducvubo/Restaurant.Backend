package com.restaurant.ddd.application.model.ledger;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class LedgerPreviewItemDTO {
    private UUID materialId;
    private String materialName;
    private String baseUnitSymbol;  // Base unit symbol for display
    private List<LedgerPreviewBatchDTO> batches;
    private BigDecimal totalQuantity;
    private BigDecimal totalAmount;
}
