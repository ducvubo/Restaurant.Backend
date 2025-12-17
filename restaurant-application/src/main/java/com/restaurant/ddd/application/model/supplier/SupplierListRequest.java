package com.restaurant.ddd.application.model.supplier;

import lombok.Data;

@Data
public class SupplierListRequest {
    private String keyword;
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;
}
