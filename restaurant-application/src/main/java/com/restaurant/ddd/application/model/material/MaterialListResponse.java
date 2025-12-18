package com.restaurant.ddd.application.model.material;

import com.restaurant.ddd.application.model.common.PageResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response for listing materials
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MaterialListResponse extends PageResponse<MaterialDTO> {
    // Can add specific fields if needed
}
