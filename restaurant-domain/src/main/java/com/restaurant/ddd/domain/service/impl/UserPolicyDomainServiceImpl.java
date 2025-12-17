package com.restaurant.ddd.domain.service.impl;

import com.restaurant.ddd.domain.model.UserPolicy;
import com.restaurant.ddd.domain.respository.UserPolicyRepository;
import com.restaurant.ddd.domain.service.UserPolicyDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserPolicyDomainServiceImpl implements UserPolicyDomainService {

    @Autowired
    private UserPolicyRepository userPolicyRepository;

    @Override
    public List<UserPolicy> findByUserId(UUID userId) {
        log.info("UserPolicy Domain Service: findByUserId - {}", userId);
        return userPolicyRepository.findByUserId(userId);
    }

    @Override
    public List<UUID> findPolicyIdsByUserId(UUID userId) {
        log.info("UserPolicy Domain Service: findPolicyIdsByUserId - {}", userId);
        return userPolicyRepository.findPolicyIdsByUserId(userId);
    }

    @Override
    public UserPolicy save(UserPolicy userPolicy) {
        log.info("UserPolicy Domain Service: save - UserId: {}, PolicyId: {}", 
                userPolicy.getUserId(), userPolicy.getPolicyId());
        return userPolicyRepository.save(userPolicy);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        log.info("UserPolicy Domain Service: deleteByUserId - {}", userId);
        userPolicyRepository.deleteByUserId(userId);
    }
}
