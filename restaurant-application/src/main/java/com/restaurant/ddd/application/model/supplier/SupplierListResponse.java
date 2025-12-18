package com.restaurant.ddd.application.model.supplier;

import com.restaurant.ddd.application.model.common.PageResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SupplierListResponse extends PageResponse<SupplierDTO> {
    // Can add specific fields if needed
}
