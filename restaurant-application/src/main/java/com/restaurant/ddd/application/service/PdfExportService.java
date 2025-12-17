package com.restaurant.ddd.application.service;

import java.util.UUID;

/**
 * Service for exporting documents to PDF format
 */
public interface PdfExportService {
    
    /**
     * Export adjustment transaction to PDF
     * @param adjustmentId ID of the adjustment transaction
     * @return PDF file as byte array
     */
    byte[] exportAdjustmentToPdf(UUID adjustmentId);
    
    /**
     * Export stock transaction (in/out) to PDF
     * @param stockTransactionId ID of the stock transaction
     * @return PDF file as byte array
     */
    byte[] exportStockTransactionToPdf(UUID stockTransactionId);
}
