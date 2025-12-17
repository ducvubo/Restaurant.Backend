package com.restaurant.ddd.domain.service.impl;

import com.restaurant.ddd.domain.model.User;
import com.restaurant.ddd.domain.respository.UserRepository;
import com.restaurant.ddd.domain.service.UserDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserDomainServiceImpl implements UserDomainService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> findById(UUID id) {
        log.info("Domain Service: findById - {}", id);
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        log.info("Domain Service: findByUsername - {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.info("Domain Service: findByEmail - {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        log.info("Domain Service: findAll");
        return userRepository.findAll();
    }

    @Override
    public User save(User user) {
        log.info("Domain Service: save - {}", user.getUsername());
        return userRepository.save(user);
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Domain Service: deleteById - {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

