package com.restaurant.ddd.application.model.ledger;

import lombok.Data;
import java.util.UUID;

@Data
public class LedgerPreviewRequest {
    private UUID transactionId;
}
