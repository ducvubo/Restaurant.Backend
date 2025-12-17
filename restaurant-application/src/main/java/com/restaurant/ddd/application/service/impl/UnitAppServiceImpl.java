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
        unit.setSymbol(request.getSymbol());
        unit.setBaseUnitId(request.getBaseUnitId());
        unit.setConversionRate(request.getConversionRate());
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
    public List<UnitDTO> getBaseUnits() {
        log.info("Unit Application Service: getBaseUnits");
        return unitRepository.findBaseUnits().stream()
                .map(UnitMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UnitListResponse getList(UnitListRequest request) {
        log.info("Unit Application Service: getList - keyword: {}, status: {}, page: {}, size: {}",
                request.getKeyword(), request.getStatus(), request.getPage(), request.getSize());

        List<Unit> allUnits = unitRepository.findAll();

        // Filter by keyword
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String keyword = request.getKeyword().toLowerCase();
            allUnits = allUnits.stream()
                    .filter(u -> (u.getCode() != null && u.getCode().toLowerCase().contains(keyword)) ||
                                (u.getName() != null && u.getName().toLowerCase().contains(keyword)) ||
                                (u.getSymbol() != null && u.getSymbol().toLowerCase().contains(keyword)))
                    .collect(Collectors.toList());
        }

        // Filter by status
        if (request.getStatus() != null) {
            allUnits = allUnits.stream()
                    .filter(u -> u.getStatus() != null && u.getStatus().code().equals(request.getStatus()))
                    .collect(Collectors.toList());
        }

        // Pagination
        int page = request.getPage() != null && request.getPage() > 0 ? request.getPage() - 1 : 0;
        int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10;
        int total = allUnits.size();
        int start = page * size;
        int end = Math.min(start + size, total);

        List<Unit> pagedUnits = start < total ? allUnits.subList(start, end) : new ArrayList<>();

        UnitListResponse response = new UnitListResponse();
        response.setItems(pagedUnits.stream().map(UnitMapper::toDTO).collect(Collectors.toList()));
        response.setPage(request.getPage() != null && request.getPage() > 0 ? request.getPage() : 1);
        response.setSize(size);
        response.setTotal((long) total);

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
        unit.setSymbol(request.getSymbol());
        unit.setBaseUnitId(request.getBaseUnitId());
        unit.setConversionRate(request.getConversionRate());
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
