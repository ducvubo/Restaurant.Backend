package com.restaurant.ddd.controller.http.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cấu hình WebMVC để đăng ký PolicyAuthorizationHandler.
 * Handler này sẽ kiểm tra quyền động (policy) cho các request.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private PolicyAuthorizationHandler policyAuthorizationHandler;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Đăng ký PolicyAuthorizationHandler để kiểm tra quyền
        // Hiện tại đã disable permission check, nhưng cấu trúc đã sẵn sàng để bật lại
        // registry.addInterceptor(policyAuthorizationHandler)
        //         .addPathPatterns("/api/management/**");
    }
}

