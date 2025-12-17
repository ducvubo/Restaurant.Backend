package com.restaurant.ddd.application.mapper;

import com.restaurant.ddd.application.model.unit.UnitDTO;
import com.restaurant.ddd.domain.model.Unit;

/**
 * Mapper between Unit domain model and UnitDTO
 */
public class UnitMapper {

    public static UnitDTO toDTO(Unit unit) {
        if (unit == null) {
            return null;
        }

        UnitDTO dto = new UnitDTO();
        dto.setId(unit.getId());
        dto.setCode(unit.getCode());
        dto.setName(unit.getName());
        dto.setSymbol(unit.getSymbol());
        dto.setBaseUnitId(unit.getBaseUnitId());
        dto.setConversionRate(unit.getConversionRate());
        dto.setDescription(unit.getDescription());
        dto.setStatus(unit.getStatus() != null ? unit.getStatus().code() : null);
        dto.setCreatedBy(unit.getCreatedBy());
        dto.setUpdatedBy(unit.getUpdatedBy());
        dto.setCreatedDate(unit.getCreatedDate());
        dto.setUpdatedDate(unit.getUpdatedDate());

        return dto;
    }
}
