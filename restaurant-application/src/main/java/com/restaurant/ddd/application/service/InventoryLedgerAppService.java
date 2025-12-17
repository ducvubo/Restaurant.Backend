package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.ledger.InventoryLedgerListRequest;
import com.restaurant.ddd.application.model.ledger.InventoryLedgerListResponse;
import com.restaurant.ddd.domain.model.ResultMessage;

import java.math.BigDecimal;
import java.util.UUID;

public interface InventoryLedgerAppService {
    ResultMessage<InventoryLedgerListResponse> getList(InventoryLedgerListRequest request);
    BigDecimal getAvailableStock(UUID warehouseId, UUID materialId);
    ResultMessage<BigDecimal> getCurrentStock(UUID warehouseId, UUID materialId);
}
