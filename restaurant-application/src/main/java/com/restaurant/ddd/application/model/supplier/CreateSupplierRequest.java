package com.restaurant.ddd.application.model.supplier;

import lombok.Data;

/**
 * Request for creating Supplier
 */
@Data
public class CreateSupplierRequest {
    private String code;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String taxCode;
    private String paymentTerms;
    private Integer rating;
    private String notes;
}
