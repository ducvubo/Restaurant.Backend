package com.restaurant.ddd.domain.service.impl;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Policy;
import com.restaurant.ddd.domain.respository.PolicyRepository;
import com.restaurant.ddd.domain.service.PolicyDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PolicyDomainServiceImpl implements PolicyDomainService {

    @Autowired
    private PolicyRepository policyRepository;

    @Override
    public Optional<Policy> findById(UUID id) {
        log.info("Policy Domain Service: findById - {}", id);
        return policyRepository.findById(id);
    }

    @Override
    public List<Policy> findAll() {
        log.info("Policy Domain Service: findAll");
        return policyRepository.findAll();
    }

    @Override
    public List<Policy> findByStatus(DataStatus status) {
        log.info("Policy Domain Service: findByStatus - {}", status);
        return policyRepository.findByStatus(status);
    }

    @Override
    public Policy save(Policy policy) {
        log.info("Policy Domain Service: save - {}", policy.getName());
        return policyRepository.save(policy); // Business Rule Validations should happen inside Policy methods before here
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Policy Domain Service: deleteById - {}", id);
        policyRepository.deleteById(id);
    }
}
