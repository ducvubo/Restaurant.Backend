package com.restaurant.ddd.application.model.user;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class PolicyDTO {
    private UUID id;
    private String name;
    private String description;
    private List<String> policies;
    private DataStatus status;
    private UUID createdBy;
    private UUID updatedBy;
    private UUID deletedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime deletedDate;
}

