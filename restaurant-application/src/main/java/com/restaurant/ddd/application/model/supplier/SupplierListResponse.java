package com.restaurant.ddd.application.model.supplier;

import lombok.Data;

import java.util.List;

@Data
public class SupplierListResponse {
    private List<SupplierDTO> items;
    private Integer page;
    private Integer size;
    private Long total;
}
