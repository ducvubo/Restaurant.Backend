package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.ledger.LedgerPreviewResponse;
import com.restaurant.ddd.application.model.stock.StockInRequest;
import com.restaurant.ddd.application.model.stock.StockTransactionDTO;
import com.restaurant.ddd.application.model.stock.StockTransactionListRequest;
import com.restaurant.ddd.application.model.stock.StockTransactionListResponse;
import com.restaurant.ddd.domain.model.ResultMessage;

import java.util.UUID;

/**
 * Application Service for Stock In operations
 * Handles all business logic related to stock in transactions
 */
public interface StockInAppService {
    
    /**
     * Create a new stock in transaction (draft mode)
     * @param request Stock in request data
     * @return Created transaction DTO
     */
    ResultMessage<StockTransactionDTO> create(StockInRequest request);
    
    /**
     * Update an existing stock in transaction (only if not locked)
     * @param id Transaction ID
     * @param request Updated stock in data
     * @return Updated transaction DTO
     */
    ResultMessage<StockTransactionDTO> update(UUID id, StockInRequest request);
    
    /**
     * Get stock in transaction by ID
     * @param id Transaction ID
     * @return Transaction DTO with items
     */
    ResultMessage<StockTransactionDTO> getById(UUID id);
    
    /**
     * Get paginated list of stock in transactions
     * @param request List request with filters
     * @return Paginated response
     */
    ResultMessage<StockTransactionListResponse> getList(StockTransactionListRequest request);
    
    /**
     * Lock a stock in transaction (finalize and post to ledger)
     * @param id Transaction ID
     * @return Success message
     */
    ResultMessage<String> lock(UUID id);
    
    /**
     * Unlock a stock in transaction (reverse ledger entries)
     * @param id Transaction ID
     * @return Success message
     */
    ResultMessage<String> unlock(UUID id);
    
    /**
     * Preview ledger entries before locking
     * @param id Transaction ID
     * @return Preview data
     */
    ResultMessage<LedgerPreviewResponse> previewLedger(UUID id);
}
