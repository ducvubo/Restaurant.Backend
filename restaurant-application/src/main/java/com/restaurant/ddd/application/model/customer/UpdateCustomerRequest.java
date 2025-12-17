package com.restaurant.ddd.application.model.customer;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateCustomerRequest {
    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String taxCode;
    private Integer customerType;
}
