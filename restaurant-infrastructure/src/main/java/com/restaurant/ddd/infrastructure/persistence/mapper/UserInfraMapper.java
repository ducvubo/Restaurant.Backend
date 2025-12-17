package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.model.User;
import com.restaurant.ddd.infrastructure.persistence.entity.UserManagementJpaEntity;

public class UserInfraMapper {
    public static User toDomain(UserManagementJpaEntity jpa) {
        if (jpa == null) return null;
        return new User()
                .setId(jpa.getId())
                .setUsername(jpa.getUsername())
                .setEmail(jpa.getEmail())
                .setPassword(jpa.getPassword())
                .setFullName(jpa.getFullName())
                .setPhone(jpa.getPhone())
                .setAddress(jpa.getAddress())
                .setCreatedBy(jpa.getCreatedBy())
                .setUpdatedBy(jpa.getUpdatedBy())
                .setDeletedBy(jpa.getDeletedBy())
                .setCreatedDate(jpa.getCreatedDate())
                .setUpdatedDate(jpa.getUpdatedDate())
                .setDeletedDate(jpa.getDeletedDate())
                .setStatus(jpa.getStatus());
    }

    public static UserManagementJpaEntity toJpa(User domain) {
        if (domain == null) return null;
        UserManagementJpaEntity jpa = new UserManagementJpaEntity();
        jpa.setId(domain.getId());
        jpa.setUsername(domain.getUsername());
        jpa.setEmail(domain.getEmail());
        jpa.setPassword(domain.getPassword());
        jpa.setFullName(domain.getFullName());
        jpa.setPhone(domain.getPhone());
        jpa.setAddress(domain.getAddress());
        jpa.setCreatedBy(domain.getCreatedBy());
        jpa.setUpdatedBy(domain.getUpdatedBy());
        jpa.setDeletedBy(domain.getDeletedBy());
        jpa.setCreatedDate(domain.getCreatedDate());
        jpa.setUpdatedDate(domain.getUpdatedDate());
        jpa.setDeletedDate(domain.getDeletedDate());
        jpa.setStatus(domain.getStatus());
        return jpa;
    }
}
