package com.restaurant.ddd.application.mapper;

import com.restaurant.ddd.application.model.user.CreateUserRequest;
import com.restaurant.ddd.application.model.user.UpdateUserRequest;
import com.restaurant.ddd.application.model.user.UserDTO;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.User;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setCreatedBy(user.getCreatedBy());
        dto.setUpdatedBy(user.getUpdatedBy());
        dto.setDeletedBy(user.getDeletedBy());
        dto.setCreatedDate(user.getCreatedDate());
        dto.setUpdatedDate(user.getUpdatedDate());
        dto.setDeletedDate(user.getDeletedDate());
        dto.setStatus(user.getStatus());
        return dto;
    }

    public static User toDomain(CreateUserRequest request, String encodedPassword) {
        if (request == null) {
            return null;
        }
        User user = new User();
        user.setId(null);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword); // Already hashed password
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setStatus(DataStatus.ACTIVE);
        return user;
    }

    public static void updateDomain(User user, UpdateUserRequest request) {
        if (request == null || user == null) {
            return;
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
    }
}

