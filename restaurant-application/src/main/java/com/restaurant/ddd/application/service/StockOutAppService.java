package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.ledger.LedgerPreviewResponse;
import com.restaurant.ddd.application.model.stock.StockOutRequest;
import com.restaurant.ddd.application.model.stock.StockTransactionDTO;
import com.restaurant.ddd.application.model.stock.StockTransactionListRequest;
import com.restaurant.ddd.application.model.stock.StockTransactionListResponse;
import com.restaurant.ddd.domain.model.ResultMessage;

import java.util.UUID;

/**
 * Application Service for Stock Out operations
 * Handles all business logic related to stock out transactions
 */
public interface StockOutAppService {
    
    /**
     * Create a new stock out transaction (draft mode)
     * @param request Stock out request data
     * @return Created transaction DTO
     */
    ResultMessage<StockTransactionDTO> create(StockOutRequest request);
    
    /**
     * Update an existing stock out transaction (only if not locked)
     * @param id Transaction ID
     * @param request Updated stock out data
     * @return Updated transaction DTO
     */
    ResultMessage<StockTransactionDTO> update(UUID id, StockOutRequest request);
    
    /**
     * Get stock out transaction by ID
     * @param id Transaction ID
     * @return Transaction DTO with items and batch mappings
     */
    ResultMessage<StockTransactionDTO> getById(UUID id);
    
    /**
     * Get paginated list of stock out transactions
     * @param request List request with filters
     * @return Paginated response
     */
    ResultMessage<StockTransactionListResponse> getList(StockTransactionListRequest request);
    
    /**
     * Lock a stock out transaction (finalize, deduct inventory, post to ledger)
     * @param id Transaction ID
     * @return Success message
     */
    ResultMessage<String> lock(UUID id);
    
    /**
     * Unlock a stock out transaction (reverse inventory deduction)
     * @param id Transaction ID
     * @return Success message
     */
    ResultMessage<String> unlock(UUID id);
    
    /**
     * Preview ledger entries and batch deductions before locking
     * @param id Transaction ID
     * @return Preview data with FIFO batch allocation
     */
    ResultMessage<LedgerPreviewResponse> previewLedger(UUID id);
}
