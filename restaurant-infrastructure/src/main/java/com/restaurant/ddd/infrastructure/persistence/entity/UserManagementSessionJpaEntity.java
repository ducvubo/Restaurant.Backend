package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "USER_MANAGEMENT_SESSION")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserManagementSessionJpaEntity extends BaseJpaEntity {

    @Column(name = "USER_ID", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "JIT_TOKEN", nullable = false, columnDefinition = "UUID")
    private UUID jitToken;

    @Column(name = "JIT_REFRESH_TOKEN", nullable = false, columnDefinition = "UUID")
    private UUID jitRefreshToken;

    @Column(name = "CLIENT_ID", nullable = false, length = 255)
    private String clientId;

    @Column(name = "LOGIN_TIME", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "LOGIN_IP", length = 255)
    private String loginIp;
}
