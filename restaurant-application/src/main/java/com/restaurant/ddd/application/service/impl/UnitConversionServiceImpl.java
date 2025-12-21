package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.model.unit.UnitDTO;
import com.restaurant.ddd.application.model.unitconversion.MaterialUnitDTO;
import com.restaurant.ddd.application.model.unitconversion.UnitConversionDTO;
import com.restaurant.ddd.application.model.unitconversion.UnitConversionListRequest;
import com.restaurant.ddd.application.model.unitconversion.UnitConversionListResponse;
import com.restaurant.ddd.application.model.unitconversion.UnitConversionRequest;
import com.restaurant.ddd.application.service.UnitConversionService;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.ResultCode;
import com.restaurant.ddd.domain.model.ResultMessage;
import com.restaurant.ddd.infrastructure.persistence.entity.*;
import com.restaurant.ddd.infrastructure.persistence.repository.*;
import com.restaurant.ddd.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitConversionServiceImpl implements UnitConversionService {

    private final UnitConversionJpaRepository unitConversionRepository;
    private final MaterialUnitGroupJpaRepository materialUnitGroupRepository;
    private final UnitConversionHistoryJpaRepository historyRepository;
    private final com.restaurant.ddd.infrastructure.persistence.mapper.UnitJpaRepository unitJpaRepository;
    private final com.restaurant.ddd.domain.respository.InventoryLedgerRepository inventoryLedgerRepository;

    @Override
    public BigDecimal getConversionFactor(UUID fromUnitId, UUID toUnitId) {
        if (fromUnitId.equals(toUnitId)) {
            return BigDecimal.ONE;
        }

        return unitConversionRepository
                .findByFromUnitIdAndToUnitId(fromUnitId, toUnitId)
                .filter(uc -> uc.getStatus() == DataStatus.ACTIVE)
                .map(UnitConversionJpaEntity::getConversionFactor)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Không tìm thấy hệ số chuyển đổi từ %s sang %s", 
                            fromUnitId, toUnitId)));
    }

    @Override
    public BigDecimal convertQuantity(BigDecimal quantity, UUID fromUnitId, UUID toUnitId) {
        BigDecimal factor = getConversionFactor(fromUnitId, toUnitId);
        return quantity.multiply(factor);
    }

    @Override
    public UUID getBaseUnit(UUID materialId) {
        return materialUnitGroupRepository
                .findByMaterialIdAndIsBaseUnitTrueAndStatus(materialId, DataStatus.ACTIVE)
                .map(MaterialUnitGroupJpaEntity::getUnitId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nguyên liệu chưa có đơn vị cơ sở"));
    }

    @Override
    public List<MaterialUnitDTO> getUnitsForMaterial(UUID materialId) {
        List<MaterialUnitGroupJpaEntity> groups = materialUnitGroupRepository
                .findByMaterialIdAndStatus(materialId, DataStatus.ACTIVE);

        return groups.stream()
                .map(this::toMaterialUnitDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUnitAllowedForMaterial(UUID materialId, UUID unitId) {
        return materialUnitGroupRepository
                .existsByMaterialIdAndUnitIdAndStatus(materialId, unitId, DataStatus.ACTIVE);
    }

    @Override
    @Transactional
    public ResultMessage<UnitConversionDTO> createConversion(UnitConversionRequest request) {
        // Validation
        if (request.getFromUnitId().equals(request.getToUnitId())) {
            return new ResultMessage<>(ResultCode.ERROR, 
                "Đơn vị nguồn và đích phải khác nhau", null);
        }

        if (request.getConversionFactor().compareTo(BigDecimal.ZERO) <= 0) {
            return new ResultMessage<>(ResultCode.ERROR, 
                "Hệ số chuyển đổi phải lớn hơn 0", null);
        }

        // Check if forward conversion already exists
        Optional<UnitConversionJpaEntity> existing = unitConversionRepository
                .findByFromUnitIdAndToUnitId(request.getFromUnitId(), request.getToUnitId());
        
        if (existing.isPresent() && existing.get().getStatus() == DataStatus.ACTIVE) {
            return new ResultMessage<>(ResultCode.ERROR, 
                "Hệ số chuyển đổi đã tồn tại", null);
        }

        // Check if reverse conversion exists and validate consistency
        Optional<UnitConversionJpaEntity> reverse = unitConversionRepository
                .findByFromUnitIdAndToUnitId(request.getToUnitId(), request.getFromUnitId());
        
        if (reverse.isPresent() && reverse.get().getStatus() == DataStatus.ACTIVE) {
            // Calculate expected factor based on reverse
            BigDecimal reverseFactor = reverse.get().getConversionFactor();
            BigDecimal expectedFactor = BigDecimal.ONE.divide(reverseFactor, 6, java.math.RoundingMode.HALF_UP);
            
            // Check consistency with 1% tolerance
            BigDecimal diff = request.getConversionFactor().subtract(expectedFactor).abs();
            BigDecimal tolerance = expectedFactor.multiply(new BigDecimal("0.01"));
            
            if (diff.compareTo(tolerance) > 0) {
                // Get unit names for error message
                String fromUnitName = getUnitName(request.getFromUnitId());
                String toUnitName = getUnitName(request.getToUnitId());
                
                return new ResultMessage<>(ResultCode.ERROR,
                    String.format("Hệ số không nhất quán! Đã có %s → %s (x%.6f). " +
                                 "Hệ số ngược lại nên là %.6f, bạn nhập %.6f",
                        toUnitName, fromUnitName, reverseFactor,
                        expectedFactor, request.getConversionFactor()),
                    null);
            }
        }

        // Create forward conversion
        UnitConversionJpaEntity entity = new UnitConversionJpaEntity();
        entity.setFromUnitId(request.getFromUnitId());
        entity.setToUnitId(request.getToUnitId());
        entity.setConversionFactor(request.getConversionFactor());
        entity.setStatus(DataStatus.ACTIVE);
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDate(LocalDateTime.now());

        UnitConversionJpaEntity saved = unitConversionRepository.save(entity);

        // Log history
        logHistory(saved.getId(), request.getFromUnitId(), request.getToUnitId(),
                null, request.getConversionFactor(), "CREATE", request.getReason());

        // Auto-create reverse conversion if not exists
        if (!reverse.isPresent() || reverse.get().getStatus() != DataStatus.ACTIVE) {
            UnitConversionJpaEntity reverseEntity = new UnitConversionJpaEntity();
            reverseEntity.setFromUnitId(request.getToUnitId());
            reverseEntity.setToUnitId(request.getFromUnitId());
            reverseEntity.setConversionFactor(
                BigDecimal.ONE.divide(request.getConversionFactor(), 6, java.math.RoundingMode.HALF_UP)
            );
            reverseEntity.setStatus(DataStatus.ACTIVE);
            reverseEntity.setCreatedBy(SecurityUtils.getCurrentUserId());
            reverseEntity.setCreatedDate(LocalDateTime.now());
            
            UnitConversionJpaEntity savedReverse = unitConversionRepository.save(reverseEntity);
            
            // Log reverse creation
            logHistory(savedReverse.getId(), reverseEntity.getFromUnitId(), 
                       reverseEntity.getToUnitId(), null, reverseEntity.getConversionFactor(),
                       "AUTO_CREATE_REVERSE", "Tự động tạo từ conversion ID: " + saved.getId());
        }

        return new ResultMessage<>(ResultCode.SUCCESS, 
            "Tạo hệ số chuyển đổi thành công (bao gồm cả hệ số ngược lại)", toDTO(saved));
    }
    
    // Helper method to get unit name
    private String getUnitName(UUID unitId) {
        return unitJpaRepository.findById(unitId)
                .map(UnitJpaEntity::getName)
                .orElse("Unknown");
    }

    @Override
    @Transactional
    public ResultMessage<UnitConversionDTO> updateConversion(UUID id, UnitConversionRequest request) {
        Optional<UnitConversionJpaEntity> opt = unitConversionRepository.findById(id);
        if (!opt.isPresent()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy", null);
        }

        UnitConversionJpaEntity entity = opt.get();
        
        // VALIDATION: Cannot change units, only conversion factor
        if (request.getFromUnitId() != null && !request.getFromUnitId().equals(entity.getFromUnitId())) {
            return new ResultMessage<>(ResultCode.ERROR,
                    "Không thể thay đổi đơn vị nguồn khi chỉnh sửa. Chỉ được phép sửa hệ số chuyển đổi.",
                    null);
        }
        if (request.getToUnitId() != null && !request.getToUnitId().equals(entity.getToUnitId())) {
            return new ResultMessage<>(ResultCode.ERROR,
                    "Không thể thay đổi đơn vị đích khi chỉnh sửa. Chỉ được phép sửa hệ số chuyển đổi.",
                    null);
        }
        
        BigDecimal oldFactor = entity.getConversionFactor();

        // Check usage
        long usageCount = unitConversionRepository.countUsageInLedger(
                entity.getFromUnitId(), entity.getToUnitId());

        // Update forward conversion
        entity.setConversionFactor(request.getConversionFactor());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDate(LocalDateTime.now());

        UnitConversionJpaEntity saved = unitConversionRepository.save(entity);

        // Log history
        logHistory(saved.getId(), entity.getFromUnitId(), entity.getToUnitId(),
                oldFactor, request.getConversionFactor(), "UPDATE", request.getReason());

        // Auto-update reverse conversion if exists
        Optional<UnitConversionJpaEntity> reverse = unitConversionRepository
                .findByFromUnitIdAndToUnitId(entity.getToUnitId(), entity.getFromUnitId());
        
        if (reverse.isPresent() && reverse.get().getStatus() == DataStatus.ACTIVE) {
            UnitConversionJpaEntity reverseEntity = reverse.get();
            BigDecimal oldReverseFactor = reverseEntity.getConversionFactor();
            BigDecimal newReverseFactor = BigDecimal.ONE.divide(
                request.getConversionFactor(), 6, java.math.RoundingMode.HALF_UP
            );
            
            // Update reverse
            reverseEntity.setConversionFactor(newReverseFactor);
            reverseEntity.setUpdatedBy(SecurityUtils.getCurrentUserId());
            reverseEntity.setUpdatedDate(LocalDateTime.now());
            
            unitConversionRepository.save(reverseEntity);
            
            // Log reverse update
            logHistory(reverseEntity.getId(), reverseEntity.getFromUnitId(), 
                       reverseEntity.getToUnitId(), oldReverseFactor, newReverseFactor,
                       "AUTO_UPDATE_REVERSE", 
                       "Tự động cập nhật từ conversion ID: " + saved.getId());
        }

        // Warning message
        String message = usageCount > 0
                ? String.format("Cập nhật thành công (bao gồm cả hệ số ngược lại). Có %d giao dịch đang dùng hệ số cũ.", usageCount)
                : "Cập nhật thành công (bao gồm cả hệ số ngược lại)";

        return new ResultMessage<>(ResultCode.SUCCESS, message, toDTO(saved));
    }

    @Override
    @Transactional
    public ResultMessage<Void> deleteConversion(UUID id) {
        Optional<UnitConversionJpaEntity> opt = unitConversionRepository.findById(id);
        if (!opt.isPresent()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy", null);
        }

        UnitConversionJpaEntity entity = opt.get();

        // VALIDATION: Check if conversion is used by any material
        long materialUsageCount = materialUnitGroupRepository.countByUnitIdAndStatus(
                entity.getFromUnitId(), DataStatus.ACTIVE);
        if (materialUsageCount > 0) {
            String fromUnitName = getUnitName(entity.getFromUnitId());
            String toUnitName = getUnitName(entity.getToUnitId());
            return new ResultMessage<>(ResultCode.ERROR,
                    String.format("Không thể xóa hệ số chuyển đổi %s → %s vì có %d nguyên liệu đang sử dụng đơn vị %s. " +
                                 "Vui lòng xóa đơn vị khỏi nguyên liệu trước.",
                            fromUnitName, toUnitName, materialUsageCount, fromUnitName),
                    null);
        }

        // Check usage
        long usageCount = unitConversionRepository.countUsageInLedger(
                entity.getFromUnitId(), entity.getToUnitId());

        if (usageCount > 0) {
            // Soft delete forward
            entity.setStatus(DataStatus.DELETED);
            entity.setDeletedBy(SecurityUtils.getCurrentUserId());
            entity.setDeletedDate(LocalDateTime.now());
            unitConversionRepository.save(entity);

            // Log history
            logHistory(entity.getId(), entity.getFromUnitId(), entity.getToUnitId(),
                    entity.getConversionFactor(), null, "DELETE", 
                    String.format("Soft delete do có %d giao dịch sử dụng", usageCount));

            // Auto soft-delete reverse if exists
            Optional<UnitConversionJpaEntity> reverse = unitConversionRepository
                    .findByFromUnitIdAndToUnitId(entity.getToUnitId(), entity.getFromUnitId());
            
            if (reverse.isPresent() && reverse.get().getStatus() == DataStatus.ACTIVE) {
                UnitConversionJpaEntity reverseEntity = reverse.get();
                reverseEntity.setStatus(DataStatus.DELETED);
                reverseEntity.setDeletedBy(SecurityUtils.getCurrentUserId());
                reverseEntity.setDeletedDate(LocalDateTime.now());
                unitConversionRepository.save(reverseEntity);
                
                logHistory(reverseEntity.getId(), reverseEntity.getFromUnitId(), 
                           reverseEntity.getToUnitId(), reverseEntity.getConversionFactor(), null,
                           "AUTO_DELETE_REVERSE", 
                           "Tự động xóa do conversion ID: " + entity.getId() + " bị xóa");
            }

            return new ResultMessage<>(ResultCode.SUCCESS,
                    String.format("Đã vô hiệu hóa hệ số chuyển đổi (bao gồm cả hệ số ngược lại). " +
                            "Vẫn giữ lại để hiển thị %d giao dịch cũ.", usageCount),
                    null);
        }

        // Hard delete forward
        unitConversionRepository.delete(entity);
        
        // Auto hard-delete reverse if exists
        Optional<UnitConversionJpaEntity> reverse = unitConversionRepository
                .findByFromUnitIdAndToUnitId(entity.getToUnitId(), entity.getFromUnitId());
        
        if (reverse.isPresent() && reverse.get().getStatus() == DataStatus.ACTIVE) {
            unitConversionRepository.delete(reverse.get());
        }
        
        return new ResultMessage<>(ResultCode.SUCCESS, 
                "Xóa thành công (bao gồm cả hệ số ngược lại)", null);
    }

    @Override
    public ResultMessage<List<UnitConversionDTO>> listConversions() {
        List<UnitConversionJpaEntity> entities = unitConversionRepository
                .findByStatus(DataStatus.ACTIVE);

        List<UnitConversionDTO> dtos = entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new ResultMessage<>(ResultCode.SUCCESS, "Lấy danh sách thành công", dtos);
    }

    @Override
    public UnitConversionListResponse getList(UnitConversionListRequest request) {
        // Build Pageable with sorting
        String sortField = request.getSortBy() != null ? request.getSortBy() : "createdDate";
        String sortDirection = request.getSortDirection() != null ? request.getSortDirection() : "desc";
        
        org.springframework.data.domain.Sort.Direction direction = 
            "asc".equalsIgnoreCase(sortDirection) 
                ? org.springframework.data.domain.Sort.Direction.ASC 
                : org.springframework.data.domain.Sort.Direction.DESC;
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            request.getPage() != null ? request.getPage() - 1 : 0,
            request.getSafeSize(),
            org.springframework.data.domain.Sort.by(direction, sortField)
        );
        
        // Build specification for database filtering
        org.springframework.data.jpa.domain.Specification<UnitConversionJpaEntity> spec = 
            com.restaurant.ddd.infrastructure.persistence.specification.UnitConversionSpecification.buildSpec(
                request.getStatus(),
                request.getFromUnitId(),
                request.getToUnitId()
            );
        
        // Query with pagination
        org.springframework.data.domain.Page<UnitConversionJpaEntity> page = 
            unitConversionRepository.findAll(spec, pageable);
        
        // Map to DTOs
        List<UnitConversionDTO> dtos = page.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        // Post-filter by keyword if needed (since we can't join in Specification)
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String keyword = request.getKeyword().toLowerCase();
            dtos = dtos.stream()
                    .filter(dto -> {
                        String fromUnitName = dto.getFromUnitName() != null ? dto.getFromUnitName().toLowerCase() : "";
                        String fromUnitCode = dto.getFromUnitSymbol() != null ? dto.getFromUnitSymbol().toLowerCase() : "";
                        String toUnitName = dto.getToUnitName() != null ? dto.getToUnitName().toLowerCase() : "";
                        String toUnitCode = dto.getToUnitSymbol() != null ? dto.getToUnitSymbol().toLowerCase() : "";
                        
                        return fromUnitName.contains(keyword) || fromUnitCode.contains(keyword) ||
                               toUnitName.contains(keyword) || toUnitCode.contains(keyword);
                    })
                    .collect(Collectors.toList());
        }
        
        // Build response
        UnitConversionListResponse response = new UnitConversionListResponse();
        response.setItems(dtos);
        response.setTotal(page.getTotalElements());
        response.setPage(request.getPage() != null ? request.getPage() : 1);
        response.setSize(request.getSafeSize());
        response.setTotalPages(page.getTotalPages());

        return response;
    }

    @Override
    @Transactional
    public ResultMessage<MaterialUnitDTO> addUnitToMaterial(UUID materialId, UUID unitId, Boolean isBaseUnit) {
        // Check if already exists
        if (materialUnitGroupRepository.existsByMaterialIdAndUnitIdAndStatus(
                materialId, unitId, DataStatus.ACTIVE)) {
            return new ResultMessage<>(ResultCode.ERROR, "Đơn vị đã được thêm", null);
        }

        // Get existing units for this material
        List<MaterialUnitGroupJpaEntity> existingUnits = materialUnitGroupRepository
                .findByMaterialIdAndStatus(materialId, DataStatus.ACTIVE);
        
        // Find current base unit
        Optional<MaterialUnitGroupJpaEntity> currentBaseUnit = existingUnits.stream()
                .filter(u -> Boolean.TRUE.equals(u.getIsBaseUnit()))
                .findFirst();
        
        // VALIDATION: First unit must be base unit
        if (existingUnits.isEmpty()) {
            if (!Boolean.TRUE.equals(isBaseUnit)) {
                return new ResultMessage<>(ResultCode.ERROR,
                        "Nguyên liệu chưa có đơn vị cơ sở. Vui lòng chọn 'Đơn Vị Cơ Sở' cho đơn vị đầu tiên",
                        null);
            }
        } else {
            // VALIDATION: Cannot have multiple base units
            if (Boolean.TRUE.equals(isBaseUnit)) {
                if (currentBaseUnit.isPresent()) {
                    return new ResultMessage<>(ResultCode.ERROR,
                            "Nguyên liệu đã có đơn vị cơ sở",
                            null);
                }
            } else {
                // VALIDATION: Non-base unit must have conversion to base unit
                if (currentBaseUnit.isPresent()) {
                    UUID baseUnitId = currentBaseUnit.get().getUnitId();
                    try {
                        getConversionFactor(unitId, baseUnitId);
                    } catch (IllegalArgumentException e) {
                        String unitName = getUnitName(unitId);
                        String baseUnitName = getUnitName(baseUnitId);
                        return new ResultMessage<>(ResultCode.ERROR,
                                String.format("Chưa có hệ số chuyển đổi từ %s sang %s (đơn vị cơ sở). " +
                                             "Vui lòng tạo hệ số chuyển đổi trước.",
                                        unitName, baseUnitName),
                                null);
                    }
                }
            }
        }

        // If setting as base unit, check if material has transactions
        if (Boolean.TRUE.equals(isBaseUnit)) {
            long transactionCount = materialUnitGroupRepository.countTransactionsByMaterial(materialId);
            if (transactionCount > 0) {
                return new ResultMessage<>(ResultCode.ERROR,
                        "Không thể thay đổi đơn vị cơ sở khi đã có giao dịch", null);
            }

            // Unset other base units
            List<MaterialUnitGroupJpaEntity> existingGroups = materialUnitGroupRepository
                    .findByMaterialIdAndStatus(materialId, DataStatus.ACTIVE);
            for (MaterialUnitGroupJpaEntity group : existingGroups) {
                if (Boolean.TRUE.equals(group.getIsBaseUnit())) {
                    group.setIsBaseUnit(false);
                    materialUnitGroupRepository.save(group);
                }
            }
        }

        // Create
        MaterialUnitGroupJpaEntity entity = new MaterialUnitGroupJpaEntity();
        entity.setMaterialId(materialId);
        entity.setUnitId(unitId);
        entity.setIsBaseUnit(isBaseUnit != null ? isBaseUnit : false);
        entity.setStatus(DataStatus.ACTIVE);
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDate(LocalDateTime.now());

        MaterialUnitGroupJpaEntity saved = materialUnitGroupRepository.save(entity);

        return new ResultMessage<>(ResultCode.SUCCESS, 
            "Thêm đơn vị thành công", toMaterialUnitDTO(saved));
    }

    @Override
    @Transactional
    public ResultMessage<Void> removeUnitFromMaterial(UUID materialId, UUID unitId) {
        List<MaterialUnitGroupJpaEntity> groups = materialUnitGroupRepository
                .findByMaterialIdAndStatus(materialId, DataStatus.ACTIVE);

        MaterialUnitGroupJpaEntity toRemove = groups.stream()
                .filter(g -> g.getUnitId().equals(unitId))
                .findFirst()
                .orElse(null);

        if (toRemove == null) {
            return new ResultMessage<>(ResultCode.ERROR, "Đơn vị không tồn tại", null);
        }

        if (Boolean.TRUE.equals(toRemove.getIsBaseUnit())) {
            return new ResultMessage<>(ResultCode.ERROR, 
                "Không thể xóa đơn vị cơ sở", null);
        }
        
        // Check if unit has been used in inventory ledger for this material
        long usageCount = inventoryLedgerRepository.countByMaterialIdAndOriginalUnitId(materialId, unitId);
        if (usageCount > 0) {
            String unitName = unitJpaRepository.findById(unitId)
                .map(u -> u.getName())
                .orElse("Unknown");
            return new ResultMessage<>(ResultCode.ERROR,
                String.format("Đơn vị '%s' đã được sử dụng trong %d phiếu nhập/xuất. Không thể xóa.", 
                    unitName, usageCount),
                null);
        }

        // Soft delete
        toRemove.setStatus(DataStatus.DELETED);
        materialUnitGroupRepository.save(toRemove);

        return new ResultMessage<>(ResultCode.SUCCESS, "Xóa đơn vị thành công", null);
    }

    @Override
    @Transactional
    public ResultMessage<Void> setBaseUnit(UUID materialId, UUID unitId) {
        // Check if material has transactions
        long transactionCount = materialUnitGroupRepository.countTransactionsByMaterial(materialId);
        if (transactionCount > 0) {
            return new ResultMessage<>(ResultCode.ERROR,
                    String.format("Không thể thay đổi đơn vị cơ sở. " +
                            "Nguyên liệu đã có %d giao dịch.", transactionCount),
                    null);
        }

        // Unset all base units
        List<MaterialUnitGroupJpaEntity> groups = materialUnitGroupRepository
                .findByMaterialIdAndStatus(materialId, DataStatus.ACTIVE);

        for (MaterialUnitGroupJpaEntity group : groups) {
            if (group.getUnitId().equals(unitId)) {
                group.setIsBaseUnit(true);
            } else {
                group.setIsBaseUnit(false);
            }
            materialUnitGroupRepository.save(group);
        }

        return new ResultMessage<>(ResultCode.SUCCESS, "Cập nhật đơn vị cơ sở thành công", null);
    }

    // Helper methods
    private void logHistory(UUID conversionId, UUID fromUnitId, UUID toUnitId,
                           BigDecimal oldFactor, BigDecimal newFactor, 
                           String changeType, String reason) {
        UnitConversionHistoryJpaEntity history = new UnitConversionHistoryJpaEntity();
        history.setUnitConversionId(conversionId);
        history.setFromUnitId(fromUnitId);
        history.setToUnitId(toUnitId);
        history.setOldFactor(oldFactor);
        history.setNewFactor(newFactor);
        history.setChangeType(changeType);
        history.setReason(reason);
        history.setChangedBy(SecurityUtils.getCurrentUserId());
        history.setChangedDate(LocalDateTime.now());

        historyRepository.save(history);
    }

    private UnitConversionDTO toDTO(UnitConversionJpaEntity entity) {
        UnitConversionDTO dto = new UnitConversionDTO();
        dto.setId(entity.getId());
        dto.setFromUnitId(entity.getFromUnitId());
        dto.setToUnitId(entity.getToUnitId());
        dto.setConversionFactor(entity.getConversionFactor());
        dto.setStatus(entity.getStatus().name());

        // Load unit names
        unitJpaRepository.findById(entity.getFromUnitId()).ifPresent(unit -> {
            dto.setFromUnitName(unit.getName());
            dto.setFromUnitSymbol(unit.getCode());
        });

        unitJpaRepository.findById(entity.getToUnitId()).ifPresent(unit -> {
            dto.setToUnitName(unit.getName());
            dto.setToUnitSymbol(unit.getCode());
        });

        // Count usage
        long usageCount = unitConversionRepository.countUsageInLedger(
                entity.getFromUnitId(), entity.getToUnitId());
        dto.setUsageCount(usageCount);

        return dto;
    }

    private MaterialUnitDTO toMaterialUnitDTO(MaterialUnitGroupJpaEntity entity) {
        MaterialUnitDTO dto = new MaterialUnitDTO();
        dto.setId(entity.getId());
        dto.setMaterialId(entity.getMaterialId());
        dto.setUnitId(entity.getUnitId());
        dto.setIsBaseUnit(entity.getIsBaseUnit());

        // Load unit info
        unitJpaRepository.findById(entity.getUnitId()).ifPresent(unit -> {
            dto.setUnitName(unit.getName());
            dto.setUnitSymbol(unit.getCode());
        });

        // Calculate conversion factor to base unit
        if (Boolean.TRUE.equals(entity.getIsBaseUnit())) {
            // Base unit has conversion factor of 1
            dto.setConversionFactor(BigDecimal.ONE);
        } else {
            // For non-base units, get conversion factor to base unit
            UUID baseUnitId = getBaseUnit(entity.getMaterialId());
            if (baseUnitId != null) {
                try {
                    BigDecimal factor = getConversionFactor(entity.getUnitId(), baseUnitId);
                    dto.setConversionFactor(factor);
                } catch (IllegalArgumentException e) {
                    // Conversion not found, set null
                    dto.setConversionFactor(null);
                }
            }
        }

        return dto;
    }
}
