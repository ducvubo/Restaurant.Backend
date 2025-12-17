package com.restaurant.ddd.application.model.stock;

import lombok.Data;

import java.util.List;

/**
 * Response for listing stock transactions
 */
@Data
public class StockTransactionListResponse {
    private List<StockTransactionDTO> items;
    private long total;
    private int page;
    private int size;
    private int totalPages;
}
