package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.infrastructure.persistence.entity.UserManagementSessionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountSessionJpaRepository extends JpaRepository<UserManagementSessionJpaEntity, UUID> {
    @Query("SELECT a FROM UserManagementSessionJpaEntity a WHERE a.clientId = :clientId AND a.userId = :userId")
    Optional<UserManagementSessionJpaEntity> findByClientIdAndUserId(
        @Param("clientId") String clientId,
        @Param("userId") UUID userId
    );
    
    @Query("SELECT a.id FROM UserManagementSessionJpaEntity a WHERE a.jitRefreshToken = :jitRefreshToken AND a.clientId = :clientId AND a.userId = :userId")
    Optional<UUID> findByJitRefreshTokenAndClientIdAndAccId(
        @Param("jitRefreshToken") UUID jitRefreshToken,
        @Param("clientId") String clientId,
        @Param("userId") UUID userId
    );
    
    @Query("SELECT a.id FROM UserManagementSessionJpaEntity a WHERE a.jitToken = :jitToken AND a.clientId = :clientId")
    Optional<UUID> findByJitTokenAndClientId(
        @Param("jitToken") UUID jitToken,
        @Param("clientId") String clientId
    );
}
