package com.restaurant.ddd.application.model.adjustment;

import com.restaurant.ddd.application.model.common.PageResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdjustmentListResponse extends PageResponse<AdjustmentTransactionDTO> {
}
