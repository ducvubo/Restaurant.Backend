package com.restaurant.ddd.application.mapper;

import com.restaurant.ddd.application.model.material.MaterialCategoryDTO;
import com.restaurant.ddd.domain.model.MaterialCategory;
import com.restaurant.ddd.infrastructure.persistence.entity.MaterialCategoryJpaEntity;
import org.springframework.beans.BeanUtils;

public class MaterialCategoryMapper {

    public static MaterialCategoryDTO toDTO(MaterialCategory domain) {
        if (domain == null) return null;
        MaterialCategoryDTO dto = new MaterialCategoryDTO();
        BeanUtils.copyProperties(domain, dto);
        return dto;
    }

    public static MaterialCategory toDomain(MaterialCategoryJpaEntity entity) {
        if (entity == null) return null;
        MaterialCategory domain = new MaterialCategory();
        BeanUtils.copyProperties(entity, domain);
        return domain;
    }

    public static MaterialCategoryJpaEntity toEntity(MaterialCategory domain) {
        if (domain == null) return null;
        MaterialCategoryJpaEntity entity = new MaterialCategoryJpaEntity();
        BeanUtils.copyProperties(domain, entity);
        return entity;
    }
}
