package com.restaurant.ddd.application.model.branch;

import lombok.Data;

import java.util.List;

@Data
public class BranchListResponse {
    private List<BranchDTO> items;
    private Integer page;
    private Integer size;
    private Long total;
}
