package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.UserSession;
import com.restaurant.ddd.infrastructure.persistence.entity.UserManagementSessionJpaEntity;

public class UserSessionDataAccessMapper {
    
    public static UserSession toDomain(UserManagementSessionJpaEntity entity) {
        if (entity == null) return null;
        
        UserSession domain = new UserSession();
        domain.setId(entity.getId());
        domain.setUserId(entity.getUserId());
        domain.setJitToken(entity.getJitToken());
        domain.setJitRefreshToken(entity.getJitRefreshToken());
        domain.setClientId(entity.getClientId());
        domain.setLoginTime(entity.getLoginTime());
        domain.setLoginIp(entity.getLoginIp());
        domain.setCreatedBy(entity.getCreatedBy());
        domain.setUpdatedBy(entity.getUpdatedBy());
        domain.setCreatedDate(entity.getCreatedDate());
        domain.setUpdatedDate(entity.getUpdatedDate());
        domain.setStatus(entity.getStatus());
        return domain;
    }
    
    public static UserManagementSessionJpaEntity toEntity(UserSession domain) {
        if (domain == null) return null;
        
        UserManagementSessionJpaEntity entity = new UserManagementSessionJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setUserId(domain.getUserId());
        entity.setJitToken(domain.getJitToken());
        entity.setJitRefreshToken(domain.getJitRefreshToken());
        entity.setClientId(domain.getClientId());
        entity.setLoginTime(domain.getLoginTime());
        entity.setLoginIp(domain.getLoginIp());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setCreatedDate(domain.getCreatedDate());
        entity.setUpdatedDate(domain.getUpdatedDate());
        entity.setStatus(domain.getStatus());
        return entity;
    }
}
