package com.restaurant.ddd.application.model.material;

import lombok.Data;
import java.util.List;

@Data
public class MaterialCategoryListResponse {
    private List<MaterialCategoryDTO> items;
    private long total;
    private int page;
    private int size;
}
