package com.restaurant.ddd.application.model.ledger;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class LedgerPreviewResponse {
    private List<LedgerPreviewItemDTO> items;
    private BigDecimal grandTotal;
}
