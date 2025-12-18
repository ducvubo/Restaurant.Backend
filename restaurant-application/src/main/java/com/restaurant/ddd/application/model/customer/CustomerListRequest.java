package com.restaurant.ddd.application.model.customer;

import com.restaurant.ddd.application.model.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerListRequest extends PageRequest {
    private String keyword;
    private Integer status;
}
