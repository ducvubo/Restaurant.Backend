package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.model.User;
import com.restaurant.ddd.domain.respository.UserRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.UserManagementJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.UserInfraMapper;
import com.restaurant.ddd.infrastructure.persistence.mapper.UserJpaMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private UserJpaMapper userJpaMapper;

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaMapper.findById(id).map(UserInfraMapper::toDomain);
    }

    @Override
    public User save(User entity) {
        UserManagementJpaEntity saved = userJpaMapper.save(UserInfraMapper.toJpa(entity));
        return UserInfraMapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        userJpaMapper.deleteById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaMapper.findByUsername(username).map(UserInfraMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaMapper.findByEmail(email).map(UserInfraMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaMapper.findAll().stream().map(UserInfraMapper::toDomain).toList();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaMapper.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaMapper.existsByEmail(email);
    }
}

