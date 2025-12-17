package com.restaurant.ddd.application.model.user;

import lombok.Data;

import java.util.List;

@Data
public class PermissionFunction {
    private String name;
    private String key;
    private String description;
    private List<PermissionAction> actions;
}

