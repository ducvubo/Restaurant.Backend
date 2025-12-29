package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.common.PageResponse;
import com.restaurant.ddd.application.model.purchasing.*;

import java.util.UUID;

/**
 * Application Service interface for RFQ
 */
public interface RfqAppService {
    
    /**
     * Create new RFQ
     */
    RfqDTO create(RfqRequest request);
    
    /**
     * Create RFQ from approved requisition
     */
    RfqDTO createFromRequisition(UUID requisitionId, UUID supplierId);
    
    /**
     * Get RFQ by ID
     */
    RfqDTO getById(UUID id);
    
    /**
     * Get list with pagination and filters
     */
    PageResponse<RfqDTO> getList(PurchaseListRequest request);
    
    /**
     * Update RFQ (with price from supplier)
     */
    RfqDTO update(UUID id, RfqRequest request);
    
    /**
     * Send RFQ to supplier
     */
    RfqDTO send(UUID id);
    
    /**
     * Mark as received quotation
     */
    RfqDTO receiveQuotation(UUID id, RfqRequest quotation);
    
    /**
     * Accept quotation
     */
    RfqDTO accept(UUID id);
    
    /**
     * Reject quotation
     */
    RfqDTO reject(UUID id);
    
    /**
     * Cancel RFQ
     */
    RfqDTO cancel(UUID id);
    
    /**
     * Delete RFQ (only draft)
     */
    void delete(UUID id);
}
