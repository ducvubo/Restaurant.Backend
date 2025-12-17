package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.UserMapper;
import com.restaurant.ddd.application.model.user.CreateUserRequest;
import com.restaurant.ddd.application.model.user.UpdateUserRequest;
import com.restaurant.ddd.application.model.user.UserDTO;
import com.restaurant.ddd.application.model.user.UserListRequest;
import com.restaurant.ddd.application.model.user.UserListResponse;
import com.restaurant.ddd.application.service.UserAppService;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.User;
import com.restaurant.ddd.domain.model.UserPolicy;
import com.restaurant.ddd.domain.service.AuthDomainService;
import com.restaurant.ddd.domain.service.UserDomainService;
import com.restaurant.ddd.domain.service.UserPolicyDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserAppServiceImpl implements UserAppService {

    @Autowired
    private UserDomainService userDomainService;

    @Autowired
    private AuthDomainService authDomainService;

    @Autowired
    private UserPolicyDomainService userPolicyDomainService;

    private void saveUserPolicies(UUID userId, List<UUID> policyIds) {
        // Clear existing mappings then insert new ones
        userPolicyDomainService.deleteByUserId(userId);
        if (policyIds == null || policyIds.isEmpty()) {
            return;
        }
        policyIds.forEach(pid -> {
            UserPolicy upe = new UserPolicy()
                    .setUserId(userId)
                    .setPolicyId(pid);
            userPolicyDomainService.save(upe);
        });
    }

    @Override
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        log.info("Application Service: createUser - {}", request.getUsername());
        
        // Validate username uniqueness
        if (userDomainService.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        
        // Validate email uniqueness
        if (userDomainService.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        
        String encodedPassword = authDomainService.encodePassword(request.getPassword());
        User user = UserMapper.toDomain(request, encodedPassword);
        User savedUser = userDomainService.save(user);
        // Save user policies if provided
        saveUserPolicies(savedUser.getId(), request.getPolicyIds());

        UserDTO dto = UserMapper.toDTO(savedUser);
        dto.setPolicyIds(request.getPolicyIds());
        return dto;
    }

    @Override
    public UserDTO getUserById(UUID id) {
        log.info("Application Service: getUserById - {}", id);
        User user = userDomainService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + id));
        UserDTO dto = UserMapper.toDTO(user);
        dto.setPolicyIds(userPolicyDomainService.findPolicyIdsByUserId(id));
        return dto;
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        log.info("Application Service: getUserByUsername - {}", username);
        User user = userDomainService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với tên đăng nhập: " + username));
        UserDTO dto = UserMapper.toDTO(user);
        dto.setPolicyIds(userPolicyDomainService.findPolicyIdsByUserId(user.getId()));
        return dto;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        log.info("Application Service: getAllUsers");
        return userDomainService.findAll().stream()
                .map(u -> {
                    UserDTO dto = UserMapper.toDTO(u);
                    dto.setPolicyIds(userPolicyDomainService.findPolicyIdsByUserId(u.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserListResponse getList(UserListRequest request) {
        log.info("User Application Service: getList - keyword: {}, status: {}, page: {}, size: {}",
                request.getKeyword(), request.getStatus(), request.getPage(), request.getSize());
        
        List<User> allUsers = userDomainService.findAll();
        
        // Filter by keyword
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String keyword = request.getKeyword().toLowerCase();
            allUsers = allUsers.stream()
                    .filter(u -> (u.getUsername() != null && u.getUsername().toLowerCase().contains(keyword)) ||
                                (u.getEmail() != null && u.getEmail().toLowerCase().contains(keyword)) ||
                                (u.getFullName() != null && u.getFullName().toLowerCase().contains(keyword)) ||
                                (u.getPhone() != null && u.getPhone().toLowerCase().contains(keyword)) ||
                                (u.getAddress() != null && u.getAddress().toLowerCase().contains(keyword)))
                    .collect(Collectors.toList());
        }
        
        // Filter by status
        if (request.getStatus() != null) {
            allUsers = allUsers.stream()
                    .filter(u -> u.getStatus() != null && u.getStatus().code().equals(request.getStatus()))
                    .collect(Collectors.toList());
        }
        
        // Pagination
        int page = request.getPage() != null && request.getPage() > 0 ? request.getPage() - 1 : 0;
        int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10;
        int total = allUsers.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        
        List<User> pagedUsers = start < total ? allUsers.subList(start, end) : new ArrayList<>();
        
        UserListResponse response = new UserListResponse();
        response.setItems(pagedUsers.stream().map(u -> {
            UserDTO dto = UserMapper.toDTO(u);
            dto.setPolicyIds(userPolicyDomainService.findPolicyIdsByUserId(u.getId()));
            return dto;
        }).collect(Collectors.toList()));
        response.setPage(request.getPage() != null && request.getPage() > 0 ? request.getPage() : 1);
        response.setSize(size);
        response.setTotal((long) total);
        
        return response;
    }

    @Override
    @Transactional
    public UserDTO updateUser(UUID id, UpdateUserRequest request) {
        log.info("Application Service: updateUser - {}", id);
        User user = userDomainService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + id));
        
        // Validate email uniqueness if email is being updated
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userDomainService.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email đã tồn tại");
            }
        }
        
        UserMapper.updateDomain(user, request);
        User updatedUser = userDomainService.save(user);
        // Update user policies if provided (replace all)
        if (request.getPolicyIds() != null) {
            saveUserPolicies(id, request.getPolicyIds());
        }

        UserDTO dto = UserMapper.toDTO(updatedUser);
        dto.setPolicyIds(request.getPolicyIds() != null ? request.getPolicyIds() : userPolicyDomainService.findPolicyIdsByUserId(id));
        return dto;
    }

    @Override
    public UserDTO disableUser(UUID id) {
        log.info("Application Service: disableUser - {}", id);
        User user = userDomainService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setStatus(DataStatus.INACTIVE);
        User updatedUser = userDomainService.save(user);
        return UserMapper.toDTO(updatedUser);
    }

    @Override
    public UserDTO enableUser(UUID id) {
        log.info("Application Service: enableUser - {}", id);
        User user = userDomainService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setStatus(DataStatus.ACTIVE);
        User updatedUser = userDomainService.save(user);
        return UserMapper.toDTO(updatedUser);
    }
}

