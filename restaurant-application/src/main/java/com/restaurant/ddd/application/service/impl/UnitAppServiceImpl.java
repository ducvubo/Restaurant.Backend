package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.UnitMapper;
import com.restaurant.ddd.application.model.unit.*;
import com.restaurant.ddd.application.service.UnitAppService;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Unit;
import com.restaurant.ddd.domain.respository.UnitRepository;
import com.restaurant.ddd.infrastructure.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of UnitAppService
 */
@Service
@Slf4j
public class UnitAppServiceImpl implements UnitAppService {

    @Autowired
    private UnitRepository unitRepository;

    @Override
    @Transactional
    public UnitDTO createUnit(CreateUnitRequest request) {
        log.info("Unit Application Service: createUnit - {}", request.getCode());

        // Validate code uniqueness
        if (unitRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã đơn vị đã tồn tại: " + request.getCode());
        }

        // Create domain model
        Unit unit = new Unit();
        unit.setCode(request.getCode());
        unit.setName(request.getName());
        unit.setDescription(request.getDescription());
        unit.setStatus(DataStatus.ACTIVE);
        unit.setCreatedBy(SecurityUtils.getCurrentUserId());
        unit.setUpdatedBy(SecurityUtils.getCurrentUserId());

        // Validate
        unit.validate();

        // Save
        Unit savedUnit = unitRepository.save(unit);

        return UnitMapper.toDTO(savedUnit);
    }

    @Override
    public UnitDTO getUnitById(UUID id) {
        log.info("Unit Application Service: getUnitById - {}", id);
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn vị với id: " + id));
        return UnitMapper.toDTO(unit);
    }

    @Override
    public List<UnitDTO> getAllUnits() {
        log.info("Unit Application Service: getAllUnits");
        return unitRepository.findAll().stream()
                .map(UnitMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UnitListResponseNew getList(UnitListRequestNew request) {
        log.info("Unit Application Service: getList - keyword: {}, status: {}, page: {}, size: {}",
                request.getKeyword(), request.getStatus(), request.getPage(), request.getSize());

        // Build Pageable with sorting
        String sortField = request.getSortBy() != null ? request.getSortBy() : "createdDate";
        String sortDirection = request.getSortDirection() != null ? request.getSortDirection() : "desc";
        
        org.springframework.data.domain.Sort.Direction direction = 
            "asc".equalsIgnoreCase(sortDirection) 
                ? org.springframework.data.domain.Sort.Direction.ASC 
                : org.springframework.data.domain.Sort.Direction.DESC;
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            request.getPage() - 1,
            request.getSize(),
            org.springframework.data.domain.Sort.by(direction, sortField)
        );
        
        // Call repository with filters
        org.springframework.data.domain.Page<Unit> page = unitRepository.findAll(
            request.getKeyword(),
            request.getStatus(),
            pageable
        );

        // Map to DTOs
        UnitListResponseNew response = new UnitListResponseNew();
        response.setItems(page.getContent().stream().map(UnitMapper::toDTO).collect(Collectors.toList()));
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setTotal(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());

        return response;
    }

    @Override
    @Transactional
    public UnitDTO updateUnit(UUID id, UpdateUnitRequest request) {
        log.info("Unit Application Service: updateUnit - {}", id);

        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn vị với id: " + id));

        // Check code uniqueness if changed
        if (!unit.getCode().equals(request.getCode()) && unitRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã đơn vị đã tồn tại: " + request.getCode());
        }

        // Update fields
        unit.setCode(request.getCode());
        unit.setName(request.getName());
        unit.setDescription(request.getDescription());
        unit.setUpdatedBy(SecurityUtils.getCurrentUserId());

        // Validate
        unit.validate();

        // Save
        Unit updatedUnit = unitRepository.save(unit);

        return UnitMapper.toDTO(updatedUnit);
    }

    @Override
    @Transactional
    public UnitDTO activateUnit(UUID id) {
        log.info("Unit Application Service: activateUnit - {}", id);

        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn vị với id: " + id));

        unit.activate();
        unit.setUpdatedBy(SecurityUtils.getCurrentUserId());

        Unit updatedUnit = unitRepository.save(unit);

        return UnitMapper.toDTO(updatedUnit);
    }

    @Override
    @Transactional
    public UnitDTO deactivateUnit(UUID id) {
        log.info("Unit Application Service: deactivateUnit - {}", id);

        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn vị với id: " + id));

        unit.deactivate();
        unit.setUpdatedBy(SecurityUtils.getCurrentUserId());

        Unit updatedUnit = unitRepository.save(unit);

        return UnitMapper.toDTO(updatedUnit);
    }
}
