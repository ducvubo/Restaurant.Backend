package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.ledger.LedgerPreviewResponse;
import com.restaurant.ddd.application.model.stock.*;
import com.restaurant.ddd.domain.model.ResultMessage;

import java.util.UUID;

public interface StockTransactionAppService {
    ResultMessage<StockTransactionDTO> stockIn(StockInRequest request);
    ResultMessage<StockTransactionDTO> updateStockIn(UUID id, StockInRequest request);
    
    ResultMessage<StockTransactionDTO> stockOut(StockOutRequest request);
    ResultMessage<StockTransactionDTO> updateStockOut(UUID id, StockOutRequest request);
    
    ResultMessage<StockTransactionListResponse> getList(StockTransactionListRequest request);
    ResultMessage<StockTransactionDTO> getTransaction(UUID id);
    ResultMessage<String> lockTransaction(UUID id);
    ResultMessage<String> unlockTransaction(UUID id);
    ResultMessage<LedgerPreviewResponse> previewLedger(UUID transactionId);
}
