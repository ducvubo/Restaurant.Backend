package com.restaurant.ddd.application.model.user;

import lombok.Data;

import java.util.List;

@Data
public class PolicyListResponse {
    private List<PolicyDTO> items;
    private Integer page;
    private Integer size;
    private Long total;
}

