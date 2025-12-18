package com.restaurant.ddd.application.model.stock;

import com.restaurant.ddd.application.model.common.PageResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response for listing stock transactions
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StockTransactionListResponse extends PageResponse<StockTransactionDTO> {
    // Can add specific fields if needed
}
