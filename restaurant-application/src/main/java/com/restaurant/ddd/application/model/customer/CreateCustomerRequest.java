package com.restaurant.ddd.application.model.customer;

import lombok.Data;

@Data
public class CreateCustomerRequest {
    private String name;
    private String phone;
    private String email;
    private String address;
    private String taxCode;
    private Integer customerType; // 1=Individual, 2=Company
}
