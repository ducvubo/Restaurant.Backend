package com.restaurant.ddd.application.model.unit;

import lombok.Data;

import java.util.List;

@Data
public class UnitListResponse {
    private List<UnitDTO> items;
    private Integer page;
    private Integer size;
    private Long total;
}
