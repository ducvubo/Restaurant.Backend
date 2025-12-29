package com.restaurant.ddd.application.model.purchasing;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Request for receiving goods from a purchase order
 */
@Data
public class ReceiveGoodsRequest {
    private UUID performedBy;
    private String notes;
    private List<ReceiveGoodsItemRequest> items;
    
    @Data
    public static class ReceiveGoodsItemRequest {
        private UUID poItemId;
        private BigDecimal receivedQuantity;
        private String notes;
    }
}
