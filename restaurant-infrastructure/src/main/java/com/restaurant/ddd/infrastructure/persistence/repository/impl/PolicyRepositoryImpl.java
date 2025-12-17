package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Policy;
import com.restaurant.ddd.domain.respository.PolicyRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.PolicyJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.PolicyDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.mapper.PolicyJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PolicyRepositoryImpl implements PolicyRepository {

    @Autowired
    private PolicyJpaRepository policyJpaRepository;

    @Autowired
    private PolicyDataAccessMapper policyDataAccessMapper;

    @Override
    public Optional<Policy> findById(UUID id) {
        return policyJpaRepository.findById(id)
                .map(policyDataAccessMapper::toDomain);
    }

    @Override
    public Policy save(Policy domainModel) {
        PolicyJpaEntity entity = policyDataAccessMapper.toEntity(domainModel);
        PolicyJpaEntity savedEntity = policyJpaRepository.save(entity);
        return policyDataAccessMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(UUID id) {
        policyJpaRepository.deleteById(id);
    }

    @Override
    public List<Policy> findAll() {
        return policyJpaRepository.findAll().stream()
                .map(policyDataAccessMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Policy> findByStatus(DataStatus status) {
        return policyJpaRepository.findByStatus(status).stream()
                .map(policyDataAccessMapper::toDomain)
                .collect(Collectors.toList());
    }
}
