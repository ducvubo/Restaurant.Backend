package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.common.PageResponse;
import com.restaurant.ddd.application.model.purchasing.*;

import java.util.UUID;

/**
 * Application Service interface for PurchaseOrder
 */
public interface PurchaseOrderAppService {
    
    /**
     * Create new purchase order
     */
    PurchaseOrderDTO create(PurchaseOrderRequest request);
    
    /**
     * Create PO from accepted RFQ
     */
    PurchaseOrderDTO createFromRfq(UUID rfqId);
    
    /**
     * Get PO by ID
     */
    PurchaseOrderDTO getById(UUID id);
    
    /**
     * Get list with pagination and filters
     */
    PageResponse<PurchaseOrderDTO> getList(PurchaseListRequest request);
    
    /**
     * Update PO (only draft)
     */
    PurchaseOrderDTO update(UUID id, PurchaseOrderRequest request);
    
    /**
     * Confirm PO with supplier
     */
    PurchaseOrderDTO confirm(UUID id);
    
    /**
     * Receive goods (create StockIn)
     */
    PurchaseOrderDTO receiveGoods(UUID id, ReceiveGoodsRequest request);
    
    /**
     * Cancel PO
     */
    PurchaseOrderDTO cancel(UUID id);
    
    /**
     * Delete PO (only draft)
     */
    void delete(UUID id);
}
