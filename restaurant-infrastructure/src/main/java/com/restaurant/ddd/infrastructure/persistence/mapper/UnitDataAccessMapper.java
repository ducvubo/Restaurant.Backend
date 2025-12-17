package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.Unit;
import com.restaurant.ddd.infrastructure.persistence.entity.UnitJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between Unit domain model and UnitJpaEntity
 */
@Component
public class UnitDataAccessMapper {

    /**
     * Convert JPA entity to domain model
     */
    public Unit toDomain(UnitJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Unit unit = new Unit();
        unit.setId(entity.getId());
        unit.setCode(entity.getCode());
        unit.setName(entity.getName());
        unit.setSymbol(entity.getSymbol());
        unit.setBaseUnitId(entity.getBaseUnitId());
        unit.setConversionRate(entity.getConversionRate());
        unit.setDescription(entity.getDescription());
        unit.setStatus(entity.getStatus());
        unit.setCreatedBy(entity.getCreatedBy());
        unit.setUpdatedBy(entity.getUpdatedBy());
        unit.setCreatedDate(entity.getCreatedDate());
        unit.setUpdatedDate(entity.getUpdatedDate());

        return unit;
    }

    /**
     * Convert domain model to JPA entity
     */
    public UnitJpaEntity toEntity(Unit unit) {
        if (unit == null) {
            return null;
        }

        UnitJpaEntity entity = new UnitJpaEntity();
        entity.setId(unit.getId());
        entity.setCode(unit.getCode());
        entity.setName(unit.getName());
        entity.setSymbol(unit.getSymbol());
        entity.setBaseUnitId(unit.getBaseUnitId());
        entity.setConversionRate(unit.getConversionRate());
        entity.setDescription(unit.getDescription());
        entity.setStatus(unit.getStatus());
        entity.setCreatedBy(unit.getCreatedBy());
        entity.setUpdatedBy(unit.getUpdatedBy());
        entity.setCreatedDate(unit.getCreatedDate());
        entity.setUpdatedDate(unit.getUpdatedDate());

        return entity;
    }
}
