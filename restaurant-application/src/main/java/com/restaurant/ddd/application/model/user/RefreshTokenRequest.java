package com.restaurant.ddd.application.model.user;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}

