package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.adjustment.AdjustmentListRequest;
import com.restaurant.ddd.application.model.adjustment.AdjustmentListResponse;
import com.restaurant.ddd.application.model.adjustment.AdjustmentTransactionDTO;
import com.restaurant.ddd.application.model.adjustment.AdjustmentTransactionRequest;
import com.restaurant.ddd.application.model.ledger.LedgerPreviewResponse;
import com.restaurant.ddd.domain.model.ResultMessage;

import java.util.UUID;

public interface AdjustmentTransactionAppService {
    ResultMessage<AdjustmentTransactionDTO> createAdjustment(AdjustmentTransactionRequest request);
    ResultMessage<AdjustmentTransactionDTO> updateAdjustment(UUID id, AdjustmentTransactionRequest request);
    ResultMessage<AdjustmentTransactionDTO> getAdjustment(UUID id);
    ResultMessage<AdjustmentListResponse> listAdjustments(AdjustmentListRequest request);
    ResultMessage<Void> deleteAdjustment(UUID id);
    ResultMessage<Void> lockAdjustment(UUID id);
    ResultMessage<Void> unlockAdjustment(UUID id);
    ResultMessage<LedgerPreviewResponse> previewLedger(UUID transactionId);
}
