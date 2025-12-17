package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Branch;
import com.restaurant.ddd.domain.respository.BranchRepository;
import com.restaurant.ddd.infrastructure.persistence.mapper.BranchDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.mapper.BranchJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BranchRepositoryImpl implements BranchRepository {

    @Autowired
    private BranchJpaRepository branchJpaRepository;

    @Override
    public Optional<Branch> findById(UUID id) {
        return branchJpaRepository.findById(id)
                .map(BranchDataAccessMapper::toDomain);
    }

    @Override
    public Branch save(Branch entity) {
        var jpaEntity = BranchDataAccessMapper.toEntity(entity);
        var saved = branchJpaRepository.save(jpaEntity);
        return BranchDataAccessMapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        branchJpaRepository.deleteById(id);
    }

    @Override
    public List<Branch> findAll() {
        return branchJpaRepository.findAll().stream()
                .map(BranchDataAccessMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Branch> findByStatus(DataStatus status) {
        return branchJpaRepository.findByStatus(status).stream()
                .map(BranchDataAccessMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Branch> findByCode(String code) {
        return branchJpaRepository.findByCode(code)
                .map(BranchDataAccessMapper::toDomain);
    }

    @Override
    public Optional<Branch> findByEmail(String email) {
        return branchJpaRepository.findByEmail(email)
                .map(BranchDataAccessMapper::toDomain);
    }

    @Override
    public Optional<Branch> findByPhone(String phone) {
        return branchJpaRepository.findByPhone(phone)
                .map(BranchDataAccessMapper::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return branchJpaRepository.existsByCode(code);
    }

    @Override
    public boolean existsByEmail(String email) {
        return branchJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return branchJpaRepository.existsByPhone(phone);
    }
}
