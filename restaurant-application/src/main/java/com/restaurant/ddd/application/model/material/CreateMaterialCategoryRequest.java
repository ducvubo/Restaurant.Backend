package com.restaurant.ddd.application.model.material;

import lombok.Data;

@Data
public class CreateMaterialCategoryRequest {
    private String code;
    private String name;
    private String description;
}
