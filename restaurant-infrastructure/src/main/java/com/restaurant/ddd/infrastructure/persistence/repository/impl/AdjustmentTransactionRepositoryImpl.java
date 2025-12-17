package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.model.AdjustmentTransaction;
import com.restaurant.ddd.domain.respository.AdjustmentTransactionRepository;
import com.restaurant.ddd.infrastructure.persistence.mapper.AdjustmentTransactionDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.repository.AdjustmentTransactionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AdjustmentTransactionRepositoryImpl implements AdjustmentTransactionRepository {

    private final AdjustmentTransactionJpaRepository jpaRepository;
    private final AdjustmentTransactionDataAccessMapper mapper;

    @Override
    public AdjustmentTransaction save(AdjustmentTransaction transaction) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(transaction)));
    }

    @Override
    public Optional<AdjustmentTransaction> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<AdjustmentTransaction> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
