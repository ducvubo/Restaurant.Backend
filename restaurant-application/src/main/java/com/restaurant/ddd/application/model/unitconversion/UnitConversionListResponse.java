package com.restaurant.ddd.application.model.unitconversion;

import com.restaurant.ddd.application.model.common.PageResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response for listing unit conversions
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UnitConversionListResponse extends PageResponse<UnitConversionDTO> {
    // Can add specific fields if needed
}
