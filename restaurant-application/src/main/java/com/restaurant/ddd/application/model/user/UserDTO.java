package com.restaurant.ddd.application.model.user;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Data
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private UUID createdBy;
    private UUID updatedBy;
    private UUID deletedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime deletedDate;
    private DataStatus status;
    private List<UUID> policyIds;
}

