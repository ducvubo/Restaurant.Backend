package com.restaurant.ddd.application.model.user;

import com.restaurant.ddd.application.model.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PolicyListRequest extends PageRequest {
    private String keyword;
    private Integer status;
}

