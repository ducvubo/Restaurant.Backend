package com.restaurant.ddd.application.model.unit;

import lombok.Data;

/**
 * Request for creating Unit
 */
@Data
public class CreateUnitRequest {
    private String code;
    private String name;
    private String description;
}
