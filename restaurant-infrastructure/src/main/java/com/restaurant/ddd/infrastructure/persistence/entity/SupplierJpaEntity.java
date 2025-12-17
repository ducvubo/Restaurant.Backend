package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * JPA Entity for Supplier
 */
@Entity
@Table(name = "SUPPLIERS")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SupplierJpaEntity extends BaseJpaEntity {

    @Column(name = "CODE", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "NAME", nullable = false, length = 200)
    private String name;

    @Column(name = "CONTACT_PERSON", length = 100)
    private String contactPerson;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "ADDRESS", columnDefinition = "TEXT")
    private String address;

    @Column(name = "TAX_CODE", length = 50)
    private String taxCode;

    @Column(name = "PAYMENT_TERMS", length = 200)
    private String paymentTerms;

    @Column(name = "RATING")
    private Integer rating;

    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;
}
