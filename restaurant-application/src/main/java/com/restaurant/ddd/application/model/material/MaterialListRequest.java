package com.restaurant.ddd.application.model.material;

import com.restaurant.ddd.application.model.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Request for listing materials
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MaterialListRequest extends PageRequest {
    private String keyword;
    private Integer status;
    private String category;
}
