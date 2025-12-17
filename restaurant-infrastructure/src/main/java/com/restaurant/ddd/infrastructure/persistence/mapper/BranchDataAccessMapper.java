package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.Branch;
import com.restaurant.ddd.infrastructure.persistence.entity.BranchJpaEntity;

public class BranchDataAccessMapper {
    
    public static Branch toDomain(BranchJpaEntity entity) {
        if (entity == null) return null;
        
        Branch domain = new Branch();
        domain.setId(entity.getId());
        domain.setCode(entity.getCode());
        domain.setName(entity.getName());
        domain.setEmail(entity.getEmail());
        domain.setPhone(entity.getPhone());
        domain.setAddress(entity.getAddress());
        domain.setOpeningTime(entity.getOpeningTime());
        domain.setClosingTime(entity.getClosingTime());
        domain.setCreatedBy(entity.getCreatedBy());
        domain.setUpdatedBy(entity.getUpdatedBy());
        domain.setCreatedDate(entity.getCreatedDate());
        domain.setUpdatedDate(entity.getUpdatedDate());
        domain.setStatus(entity.getStatus());
        return domain;
    }
    
    public static BranchJpaEntity toEntity(Branch domain) {
        if (domain == null) return null;
        
        BranchJpaEntity entity = new BranchJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setCode(domain.getCode());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setPhone(domain.getPhone());
        entity.setAddress(domain.getAddress());
        entity.setOpeningTime(domain.getOpeningTime());
        entity.setClosingTime(domain.getClosingTime());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setCreatedDate(domain.getCreatedDate());
        entity.setUpdatedDate(domain.getUpdatedDate());
        entity.setStatus(domain.getStatus());
        return entity;
    }
}
