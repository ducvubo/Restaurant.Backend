package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.user.CreateUserRequest;
import com.restaurant.ddd.application.model.user.UpdateUserRequest;
import com.restaurant.ddd.application.model.user.UserDTO;
import com.restaurant.ddd.application.model.user.UserListRequest;
import com.restaurant.ddd.application.model.user.UserListResponse;

import java.util.List;
import java.util.UUID;

public interface UserAppService {
    UserDTO createUser(CreateUserRequest request);
    UserDTO getUserById(UUID id);
    UserDTO getUserByUsername(String username);
    List<UserDTO> getAllUsers();
    UserListResponse getList(UserListRequest request);
    UserDTO updateUser(UUID id, UpdateUserRequest request);
    UserDTO disableUser(UUID id);
    UserDTO enableUser(UUID id);
}

