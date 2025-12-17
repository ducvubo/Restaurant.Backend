package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.UserSession;

import java.util.Optional;
import java.util.UUID;

public interface AccountSessionRepository extends BaseRepository<UserSession, UUID> {
    Optional<UserSession> findByClientIdAndAccId(String clientId, UUID accId);
    Optional<UUID> findByJitRefreshTokenAndClientIdAndAccId(UUID jitRefreshToken, String clientId, UUID accId);
    Optional<UUID> findByJitTokenAndClientId(UUID jitToken, String clientId);
}
