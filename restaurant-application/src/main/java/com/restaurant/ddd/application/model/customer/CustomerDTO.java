package com.restaurant.ddd.application.model.customer;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CustomerDTO {
    private UUID id;
    private String customerCode;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String taxCode;
    private Integer customerType; // 1=Individual, 2=Company
    private String customerTypeName;
    private Integer status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
