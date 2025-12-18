package com.restaurant.ddd.application.model.ledger;

import com.restaurant.ddd.application.model.common.PageResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InventoryLedgerListResponse extends PageResponse<InventoryLedgerDTO> {
    // Can add specific fields if needed
}
