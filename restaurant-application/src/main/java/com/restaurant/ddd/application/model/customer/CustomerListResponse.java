package com.restaurant.ddd.application.model.customer;

import lombok.Data;

import java.util.List;

@Data
public class CustomerListResponse {
    private List<CustomerDTO> items;
    private Integer total;
}
