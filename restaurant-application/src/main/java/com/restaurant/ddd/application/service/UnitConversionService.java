package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.unit.UnitDTO;
import com.restaurant.ddd.application.model.unitconversion.MaterialUnitDTO;
import com.restaurant.ddd.application.model.unitconversion.UnitConversionDTO;
import com.restaurant.ddd.application.model.unitconversion.UnitConversionListRequest;
import com.restaurant.ddd.application.model.unitconversion.UnitConversionListResponse;
import com.restaurant.ddd.application.model.unitconversion.UnitConversionRequest;
import com.restaurant.ddd.domain.model.ResultMessage;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface UnitConversionService {
    
    /**
     * Get conversion factor from one unit to another
     */
    BigDecimal getConversionFactor(UUID fromUnitId, UUID toUnitId);
    
    /**
     * Convert quantity from one unit to another
     */
    BigDecimal convertQuantity(BigDecimal quantity, UUID fromUnitId, UUID toUnitId);
    
    /**
     * Get base unit for a material
     */
    UUID getBaseUnit(UUID materialId);
    
    /**
     * Get all allowed units for a material
     */
    List<MaterialUnitDTO> getUnitsForMaterial(UUID materialId);
    
    /**
     * Check if unit is allowed for material
     */
    boolean isUnitAllowedForMaterial(UUID materialId, UUID unitId);
    
    /**
     * CRUD operations for unit conversions
     */
    ResultMessage<UnitConversionDTO> createConversion(UnitConversionRequest request);
    ResultMessage<UnitConversionDTO> updateConversion(UUID id, UnitConversionRequest request);
    ResultMessage<Void> deleteConversion(UUID id);
    ResultMessage<List<UnitConversionDTO>> listConversions();
    UnitConversionListResponse getList(UnitConversionListRequest request);
    
    /**
     * Material unit group operations
     */
    ResultMessage<MaterialUnitDTO> addUnitToMaterial(UUID materialId, UUID unitId, Boolean isBaseUnit);
    ResultMessage<Void> removeUnitFromMaterial(UUID materialId, UUID unitId);
    ResultMessage<Void> setBaseUnit(UUID materialId, UUID unitId);
}
