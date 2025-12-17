package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.model.UserPolicy;
import com.restaurant.ddd.domain.respository.UserPolicyRepository;
import com.restaurant.ddd.infrastructure.persistence.mapper.UserPolicyDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.mapper.UserPolicyJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserPolicyRepositoryImpl implements UserPolicyRepository {

    @Autowired
    private UserPolicyJpaRepository userPolicyJpaRepository;

    @Override
    public Optional<UserPolicy> findById(UUID id) {
        return userPolicyJpaRepository.findById(id)
                .map(UserPolicyDataAccessMapper::toDomain);
    }

    @Override
    public UserPolicy save(UserPolicy entity) {
        var jpaEntity = UserPolicyDataAccessMapper.toEntity(entity);
        var saved = userPolicyJpaRepository.save(jpaEntity);
        return UserPolicyDataAccessMapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        userPolicyJpaRepository.deleteById(id);
    }

    @Override
    public List<UserPolicy> findByUserId(UUID userId) {
        return userPolicyJpaRepository.findByUserId(userId).stream()
                .map(UserPolicyDataAccessMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UUID> findPolicyIdsByUserId(UUID userId) {
        return userPolicyJpaRepository.findPolicyIdsByUserId(userId);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        userPolicyJpaRepository.deleteByUserId(userId);
    }
}
