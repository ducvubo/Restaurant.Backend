package com.restaurant.ddd.application.model.supplier;

import lombok.Data;

import java.util.UUID;

/**
 * Request for updating Supplier
 */
@Data
public class UpdateSupplierRequest {
    private UUID id;
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
