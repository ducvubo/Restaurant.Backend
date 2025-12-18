package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Unit;
import com.restaurant.ddd.domain.respository.UnitRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.UnitJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.UnitDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.mapper.UnitJpaRepository;
import com.restaurant.ddd.infrastructure.persistence.specification.UnitSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of UnitRepository
 */
@Repository
@Slf4j
public class UnitRepositoryImpl implements UnitRepository {

    @Autowired
    private UnitJpaRepository unitJpaRepository;

    @Autowired
    private UnitDataAccessMapper unitDataAccessMapper;

    @Override
    public Unit save(Unit unit) {
        log.debug("Saving unit: {}", unit.getCode());
        UnitJpaEntity entity = unitDataAccessMapper.toEntity(unit);
        UnitJpaEntity savedEntity = unitJpaRepository.save(entity);
        return unitDataAccessMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Unit> findById(UUID id) {
        log.debug("Finding unit by id: {}", id);
        return unitJpaRepository.findById(id)
                .map(unitDataAccessMapper::toDomain);
    }

    @Override
    public Optional<Unit> findByCode(String code) {
        log.debug("Finding unit by code: {}", code);
        return unitJpaRepository.findByCode(code)
                .map(unitDataAccessMapper::toDomain);
    }

    @Override
    public List<Unit> findAll() {
        log.debug("Finding all units");
        return unitJpaRepository.findAll().stream()
                .map(unitDataAccessMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Unit> findAll(String keyword, Integer status, Pageable pageable) {
        return unitJpaRepository.findAll(
                UnitSpecification.buildSpec(keyword, status),
                pageable
        ).map(unitDataAccessMapper::toDomain);
    }

    @Override
    public List<Unit> findByStatus(DataStatus status) {
        log.debug("Finding units by status: {}", status);
        return unitJpaRepository.findByStatus(status).stream()
                .map(unitDataAccessMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Unit> findBaseUnits() {
        log.debug("Finding base units");
        return unitJpaRepository.findBaseUnits().stream()
                .map(unitDataAccessMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting unit by id: {}", id);
        unitJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return unitJpaRepository.existsByCode(code);
    }
}
