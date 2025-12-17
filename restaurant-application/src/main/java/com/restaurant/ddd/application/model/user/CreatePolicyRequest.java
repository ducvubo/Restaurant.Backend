package com.restaurant.ddd.application.model.user;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;

import java.util.List;

@Data
public class CreatePolicyRequest {
    private String name;
    private String description;
    private List<String> policies;
    private DataStatus status;
}

