package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.model.UserSession;
import com.restaurant.ddd.domain.respository.AccountSessionRepository;
import com.restaurant.ddd.infrastructure.persistence.mapper.AccountSessionJpaRepository;
import com.restaurant.ddd.infrastructure.persistence.mapper.UserSessionDataAccessMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AccountSessionRepositoryImpl implements AccountSessionRepository {

    @Autowired
    private AccountSessionJpaRepository accountSessionJpaRepository;

    @Override
    public Optional<UserSession> findById(UUID id) {
        return accountSessionJpaRepository.findById(id)
                .map(UserSessionDataAccessMapper::toDomain);
    }

    @Override
    public UserSession save(UserSession entity) {
        var jpaEntity = UserSessionDataAccessMapper.toEntity(entity);
        var saved = accountSessionJpaRepository.save(jpaEntity);
        return UserSessionDataAccessMapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        accountSessionJpaRepository.deleteById(id);
    }

    @Override
    public Optional<UserSession> findByClientIdAndAccId(String clientId, UUID accId) {
        return accountSessionJpaRepository.findByClientIdAndUserId(clientId, accId)
                .map(UserSessionDataAccessMapper::toDomain);
    }

    @Override
    public Optional<UUID> findByJitRefreshTokenAndClientIdAndAccId(UUID jitRefreshToken, String clientId, UUID accId) {
        return accountSessionJpaRepository.findByJitRefreshTokenAndClientIdAndAccId(jitRefreshToken, clientId, accId);
    }

    @Override
    public Optional<UUID> findByJitTokenAndClientId(UUID jitToken, String clientId) {
        return accountSessionJpaRepository.findByJitTokenAndClientId(jitToken, clientId);
    }
}
