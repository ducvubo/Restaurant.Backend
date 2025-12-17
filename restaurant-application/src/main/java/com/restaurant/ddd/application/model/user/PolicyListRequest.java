package com.restaurant.ddd.application.model.user;

import lombok.Data;

@Data
public class PolicyListRequest {
    private String keyword;
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;
}

