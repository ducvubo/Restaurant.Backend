package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalTime;


@Entity
@Table(name = "BRANCH")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BranchJpaEntity extends BaseJpaEntity {

    @Column(name = "CODE", nullable = false, length = 255)
    private String code;

    @Column(name = "NAME", nullable = false, length = 255)
    private String name;

    @Column(name = "EMAIL", nullable = false, length = 255)
    private String email;

    @Column(name = "PHONE", nullable = false, length = 255)
    private String phone;

    @Column(name = "ADDRESS", nullable = false, length = 2000)
    private String address;

    @Column(name = "OPENING_TIME", nullable = false)
    private LocalTime openingTime;

    @Column(name = "CLOSING_TIME", nullable = false)
    private LocalTime closingTime;
}

