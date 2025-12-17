package com.restaurant.ddd.application.model.material;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateMaterialCategoryRequest {
    private UUID id;
    private String code;
    private String name;
    private String description;
}
