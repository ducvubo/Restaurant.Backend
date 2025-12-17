package com.restaurant.ddd.application.model.material;

import lombok.Data;

/**
 * Request for listing materials
 */
@Data
public class MaterialListRequest {
    private Integer page;
    private Integer size;
    private String keyword;
    private Integer status;
    private String category;
}
