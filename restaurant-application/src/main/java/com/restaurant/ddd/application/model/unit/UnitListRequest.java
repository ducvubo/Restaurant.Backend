package com.restaurant.ddd.application.model.unit;

import lombok.Data;

@Data
public class UnitListRequest {
    private String keyword;
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;
}
