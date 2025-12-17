package com.restaurant.ddd.application.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PermissionAction {
    private String method;
    private String key;
    
    @JsonProperty("patchRequire")
    private List<String> patchRequire;
}

