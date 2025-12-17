package com.restaurant.ddd.infrastructure.service;

import com.restaurant.ddd.domain.model.User;
import com.restaurant.ddd.domain.model.UserSession;
import com.restaurant.ddd.domain.respository.AccountSessionRepository;
import com.restaurant.ddd.domain.respository.UserRepository;
import com.restaurant.ddd.domain.service.AuthDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AuthDomainServiceImpl implements AuthDomainService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountSessionRepository accountSessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findByUsername(String username) {
        log.info("Domain Service: findByUsername - {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public User saveUser(User user) {
        log.info("Domain Service: saveUser - {}", user.getUsername());
        return userRepository.save(user);
    }

    @Override
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public Optional<User> authenticate(UUID jitToken, String clientId, UUID accountId) {
        log.info("Domain Service: authenticate - jitToken: {}, clientId: {}, accountId: {}", jitToken, clientId, accountId);
        
        // Find session by JIT token and clientId
        Optional<UUID> sessionId = accountSessionRepository.findByJitTokenAndClientId(jitToken, clientId);
        
        if (sessionId.isEmpty() || sessionId.get().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
            return Optional.empty();
        }
        
        // Get session
        Optional<UserSession> sessionOpt = accountSessionRepository.findById(sessionId.get());
        if (sessionOpt.isEmpty()) {
            return Optional.empty();
        }
        
        UserSession session = sessionOpt.get();
        
        // Verify account ID matches
        if (!session.getUserId().equals(accountId)) {
            return Optional.empty();
        }
        
        // Get user
        return userRepository.findById(accountId);
    }
}
