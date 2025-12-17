package com.restaurant.ddd.application.model.user;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String address;
    private List<UUID> policyIds;
}

