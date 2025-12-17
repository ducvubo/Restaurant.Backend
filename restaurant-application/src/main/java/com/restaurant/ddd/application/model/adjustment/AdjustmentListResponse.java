package com.restaurant.ddd.application.model.adjustment;

import lombok.Data;

import java.util.List;

@Data
public class AdjustmentListResponse {
    private List<AdjustmentTransactionDTO> items;
    private int total;
    private int page;
    private int size;
}
