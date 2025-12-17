package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.MaterialCategory;
import com.restaurant.ddd.infrastructure.persistence.entity.MaterialCategoryJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MaterialCategoryDataAccessMapper {

    public MaterialCategory toDomain(MaterialCategoryJpaEntity entity) {
        if (entity == null) return null;
        return new MaterialCategory()
                .setId(entity.getId())
                .setCode(entity.getCode())
                .setName(entity.getName())
                .setDescription(entity.getDescription())
                .setStatus(entity.getStatus())
                .setCreatedBy(entity.getCreatedBy())
                .setUpdatedBy(entity.getUpdatedBy())
                .setCreatedDate(entity.getCreatedDate())
                .setUpdatedDate(entity.getUpdatedDate());
    }

    public MaterialCategoryJpaEntity toEntity(MaterialCategory domain) {
        if (domain == null) return null;
        MaterialCategoryJpaEntity entity = new MaterialCategoryJpaEntity();
        if (domain.getId() != null) entity.setId(domain.getId());
        entity.setCode(domain.getCode());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }

    public List<MaterialCategory> toDomainList(List<MaterialCategoryJpaEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
