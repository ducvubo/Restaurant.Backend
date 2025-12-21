package com.restaurant.ddd.application.model.unitconversion;

import com.restaurant.ddd.application.model.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * Request for listing unit conversions
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UnitConversionListRequest extends PageRequest {
    private String keyword;
    private Integer status;
    private UUID fromUnitId;
    private UUID toUnitId;
}
