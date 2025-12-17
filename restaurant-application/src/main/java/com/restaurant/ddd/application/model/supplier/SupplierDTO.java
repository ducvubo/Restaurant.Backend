package com.restaurant.ddd.application.model.supplier;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Supplier
 */
@Data
public class SupplierDTO {
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
    private Integer status;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
