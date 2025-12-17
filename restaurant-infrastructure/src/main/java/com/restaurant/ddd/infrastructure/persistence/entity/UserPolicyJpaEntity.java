package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;

/**
 * JPA Entity for mapping between User and Policy (many-to-many relationship)
 */
@Entity
@Table(name = "USER_POLICY")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPolicyJpaEntity extends BaseJpaEntity {

    @Column(name = "USER_ID", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "POLICY_ID", nullable = false, columnDefinition = "UUID")
    private UUID policyId;
}
