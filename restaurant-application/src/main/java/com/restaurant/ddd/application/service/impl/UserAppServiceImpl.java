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
import com.restaurant.ddd.domain.respository.UserRepository;
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
    private UserRepository userRepository;

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
        
        // Build Pageable with sorting
        String sortField = request.getSortBy() != null ? request.getSortBy() : "createdDate";
        String sortDirection = request.getSortDirection() != null ? request.getSortDirection() : "desc";
        
        org.springframework.data.domain.Sort.Direction direction = 
            "asc".equalsIgnoreCase(sortDirection) 
                ? org.springframework.data.domain.Sort.Direction.ASC 
                : org.springframework.data.domain.Sort.Direction.DESC;
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            request.getPage() - 1,
            request.getSize(),
            org.springframework.data.domain.Sort.by(direction, sortField)
        );
        
        // Call repository with filters
        org.springframework.data.domain.Page<User> page = userRepository.findAll(
            request.getKeyword(),
            request.getStatus(),
            pageable
        );
        
        // Map to DTOs
        UserListResponse response = new UserListResponse();
        response.setItems(page.getContent().stream().map(u -> {
            UserDTO dto = UserMapper.toDTO(u);
            dto.setPolicyIds(userPolicyDomainService.findPolicyIdsByUserId(u.getId()));
            return dto;
        }).collect(Collectors.toList()));
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setTotal(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        
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

