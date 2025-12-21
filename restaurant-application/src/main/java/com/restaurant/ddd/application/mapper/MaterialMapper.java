package com.restaurant.ddd.application.mapper;

import com.restaurant.ddd.application.model.material.MaterialDTO;
import com.restaurant.ddd.domain.model.Material;

public class MaterialMapper {

    public static MaterialDTO toDTO(Material material) {
        if (material == null) return null;

        MaterialDTO dto = new MaterialDTO();
        dto.setId(material.getId());
        dto.setCode(material.getCode());
        dto.setName(material.getName());
        dto.setCategory(material.getCategory());
        dto.setCategoryId(material.getCategoryId());
        dto.setUnitPrice(material.getUnitPrice());
        dto.setMinStockLevel(material.getMinStockLevel());
        dto.setMaxStockLevel(material.getMaxStockLevel());
        dto.setDescription(material.getDescription());
        dto.setStatus(material.getStatus() != null ? material.getStatus().code() : null);
        dto.setCreatedBy(material.getCreatedBy());
        dto.setUpdatedBy(material.getUpdatedBy());
        dto.setCreatedDate(material.getCreatedDate());
        dto.setUpdatedDate(material.getUpdatedDate());
        
        return dto;
    }
}
