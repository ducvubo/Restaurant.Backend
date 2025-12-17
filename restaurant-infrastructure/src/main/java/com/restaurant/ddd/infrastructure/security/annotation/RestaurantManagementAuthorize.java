package com.restaurant.ddd.infrastructure.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation để kiểm tra quyền động (Policy) cho trang Management.
 * Tương tự TicketManagementAuthorize trong dự án C#.
 * 
 * Sử dụng:
 * - @RestaurantManagementAuthorize() - Chỉ cần đăng nhập
 * - @RestaurantManagementAuthorize("policy1") - Cần có policy "policy1"
 * - @RestaurantManagementAuthorize({"policy1", "policy2"}) - Cần có ít nhất một trong các policy
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestaurantManagementAuthorize {
    /**
     * Danh sách các policy (permission keys) cần thiết.
     * User cần có ít nhất một trong các policy này để truy cập.
     */
    String[] value() default {};
}

