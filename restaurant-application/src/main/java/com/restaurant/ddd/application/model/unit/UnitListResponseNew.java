package com.restaurant.ddd.application.model.unit;

import com.restaurant.ddd.application.model.common.PageResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UnitListResponseNew extends PageResponse<UnitDTO> {
}
