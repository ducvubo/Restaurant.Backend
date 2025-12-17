package com.restaurant.ddd.application.model.ledger;

import lombok.Data;

import java.util.List;

@Data
public class InventoryLedgerListResponse {
    private List<InventoryLedgerDTO> items;
    private long total;
    private int page;
    private int size;
    private int totalPages;
}
