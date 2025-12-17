package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.user.LoginRequest;
import com.restaurant.ddd.application.model.user.LoginResponse;
import com.restaurant.ddd.application.model.user.RefreshTokenRequest;

public interface AuthAppService {
    LoginResponse login(LoginRequest request, String clientId, String ip);
    void logout(String refreshToken);
    LoginResponse refreshToken(RefreshTokenRequest request);
}

