package com.restaurant.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.restaurant.ddd.application.deserializer.CodeEnumDeserializer;
import com.restaurant.ddd.application.deserializer.LocalDateTimeDeserializer;
import com.restaurant.ddd.application.serializer.CodeEnumSerializer;
import com.restaurant.ddd.application.serializer.LocalDateTimeSerializer;
import com.restaurant.ddd.domain.enums.CodeEnum;
import com.restaurant.ddd.domain.enums.DataStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;

/**
 * Cấu hình Jackson để:
 * 1. Serialize/deserialize các enum implement CodeEnum thành số
 * 2. Serialize/deserialize LocalDateTime thành UTC string với Z format
 * 
 * Generic solution - không cần tạo serializer/deserializer riêng cho mỗi enum mới
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder
                .timeZone(java.util.TimeZone.getTimeZone("UTC")) // Set timezone UTC
                .build();
        
        SimpleModule module = new SimpleModule();
        
        // Đăng ký generic serializer cho CodeEnum (áp dụng cho tất cả enum implement CodeEnum)
        module.addSerializer(CodeEnum.class, new CodeEnumSerializer());
        
        // Đăng ký deserializer cụ thể cho từng enum type
        // Khi thêm enum mới implement CodeEnum, chỉ cần thêm dòng tương tự ở đây
        module.addDeserializer(DataStatus.class, new CodeEnumDeserializer<>(DataStatus.class));
        
        // Đăng ký serializer/deserializer cho LocalDateTime (UTC với Z)
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        
        objectMapper.registerModule(module);
        
        return objectMapper;
    }
}

