package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.UserPolicy;
import com.restaurant.ddd.infrastructure.persistence.entity.UserPolicyJpaEntity;

public class UserPolicyDataAccessMapper {
    
    public static UserPolicy toDomain(UserPolicyJpaEntity entity) {
        if (entity == null) return null;
        
        UserPolicy domain = new UserPolicy();
        domain.setId(entity.getId());
        domain.setUserId(entity.getUserId());
        domain.setPolicyId(entity.getPolicyId());
        domain.setCreatedBy(entity.getCreatedBy());
        domain.setUpdatedBy(entity.getUpdatedBy());
        domain.setCreatedDate(entity.getCreatedDate());
        domain.setUpdatedDate(entity.getUpdatedDate());
        domain.setStatus(entity.getStatus());
        return domain;
    }
    
    public static UserPolicyJpaEntity toEntity(UserPolicy domain) {
        if (domain == null) return null;
        
        UserPolicyJpaEntity entity = new UserPolicyJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setUserId(domain.getUserId());
        entity.setPolicyId(domain.getPolicyId());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setCreatedDate(domain.getCreatedDate());
        entity.setUpdatedDate(domain.getUpdatedDate());
        entity.setStatus(domain.getStatus());
        return entity;
    }
}
