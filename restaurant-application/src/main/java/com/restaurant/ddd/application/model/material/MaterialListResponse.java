package com.restaurant.ddd.application.model.material;

import lombok.Data;

import java.util.List;

/**
 * Response for listing materials
 */
@Data
public class MaterialListResponse {
    private List<MaterialDTO> items;
    private long total;
    private int page;
    private int size;
    private int totalPages;
}
