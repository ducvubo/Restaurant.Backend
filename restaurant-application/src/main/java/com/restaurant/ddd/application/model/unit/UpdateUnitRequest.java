package com.restaurant.ddd.application.model.unit;

import lombok.Data;

import java.util.UUID;

/**
 * Request for updating Unit
 */
@Data
public class UpdateUnitRequest {
    private UUID id;
    private String code;
    private String name;
    private String description;
}
