package com.restaurant.ddd.application.model.adjustment;

import com.restaurant.ddd.application.model.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdjustmentListRequest extends PageRequest {
    private UUID warehouseId;
    private UUID materialId;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Integer status;
}
