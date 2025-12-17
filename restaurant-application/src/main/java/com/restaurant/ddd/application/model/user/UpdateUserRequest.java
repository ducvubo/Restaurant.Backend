package com.restaurant.ddd.application.model.user;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;

import java.util.UUID;
import java.util.List;

@Data
public class UpdateUserRequest {
    private UUID id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private DataStatus status;
    private List<UUID> policyIds;
}

