package com.restaurant.ddd.application.model.material;

import lombok.Data;

@Data
public class MaterialCategoryListRequest {
    private Integer page;
    private Integer size;
    private String keyword;
}
