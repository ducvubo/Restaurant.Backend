package com.restaurant.ddd.application.model.user;

import lombok.Data;

import java.util.List;

@Data
public class PermissionModel {
    private String name;
    private String key;
    private List<PermissionFunction> functions;
    private List<PermissionAction> actions;
}

