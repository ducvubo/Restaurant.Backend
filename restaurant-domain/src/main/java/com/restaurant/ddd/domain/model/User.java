package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain User (POJO, no persistence annotations)
 */
@Data
@Accessors(chain = true)
public class User {
    private UUID id;
    private String username;
    private String email;
    private String password;
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
}


