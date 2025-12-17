package com.restaurant.ddd.infrastructure.persistence.entity;

import com.restaurant.ddd.domain.enums.CustomerType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "CUSTOMERS")
public class CustomerJpaEntity extends BaseJpaEntity {

    @Column(name = "CUSTOMER_CODE", nullable = false, unique = true, length = 50)
    private String customerCode;

    @Column(name = "NAME", nullable = false, length = 200)
    private String name;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "ADDRESS", length = 500)
    private String address;

    @Column(name = "TAX_CODE", length = 50)
    private String taxCode;

    @Column(name = "CUSTOMER_TYPE", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private CustomerType customerType;
}
