package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.Material;
import com.restaurant.ddd.infrastructure.persistence.entity.MaterialJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MaterialDataAccessMapper {

    public Material materialJpaEntityToMaterial(MaterialJpaEntity entity) {
        if (entity == null) return null;
        return new Material()
                .setId(entity.getId())
                .setCode(entity.getCode())
                .setName(entity.getName())
                .setCategory(entity.getCategory())
                .setCategoryId(entity.getCategoryId())
                .setUnitId(entity.getUnitId())
                .setUnitPrice(entity.getUnitPrice())
                .setMinStockLevel(entity.getMinStockLevel())
                .setMaxStockLevel(entity.getMaxStockLevel())
                .setDescription(entity.getDescription())
                .setStatus(entity.getStatus())
                .setCreatedBy(entity.getCreatedBy())
                .setUpdatedBy(entity.getUpdatedBy())
                .setCreatedDate(entity.getCreatedDate())
                .setUpdatedDate(entity.getCreatedDate());
    }

    public MaterialJpaEntity materialToMaterialJpaEntity(Material material) {
        if (material == null) return null;
        MaterialJpaEntity entity = new MaterialJpaEntity();
        if (material.getId() != null) entity.setId(material.getId());
        entity.setCode(material.getCode());
        entity.setName(material.getName());
        entity.setCategory(material.getCategory());
        entity.setCategoryId(material.getCategoryId());
        entity.setUnitId(material.getUnitId());
        entity.setUnitPrice(material.getUnitPrice());
        entity.setMinStockLevel(material.getMinStockLevel());
        entity.setMaxStockLevel(material.getMaxStockLevel());
        entity.setDescription(material.getDescription());
        entity.setStatus(material.getStatus());
        entity.setCreatedBy(material.getCreatedBy());
        entity.setUpdatedBy(material.getUpdatedBy());
        return entity;
    }

    public List<Material> materialJpaEntitiesToMaterials(List<MaterialJpaEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::materialJpaEntityToMaterial).collect(Collectors.toList());
    }
}
