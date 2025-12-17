package com.restaurant.ddd.application.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.restaurant.ddd.domain.enums.CodeEnum;

import java.io.IOException;

/**
 * Generic deserializer để deserialize số (code) thành enum implement CodeEnum
 * Không cần tạo deserializer riêng cho mỗi enum mới
 * 
 * Lưu ý: Deserializer này cần được đăng ký cụ thể cho từng enum type trong JacksonConfig
 */
public class CodeEnumDeserializer<T extends Enum<T> & CodeEnum> extends JsonDeserializer<T> {
    
    private final Class<T> enumClass;
    
    public CodeEnumDeserializer(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.getCurrentToken();
        
        if (token == JsonToken.VALUE_NUMBER_INT) {
            Integer code = p.getIntValue();
            // Tìm enum theo code
            for (T enumValue : enumClass.getEnumConstants()) {
                if (enumValue.code().equals(code)) {
                    return enumValue;
                }
            }
            throw new IOException("Unknown " + enumClass.getSimpleName() + " code: " + code);
        } else if (token == JsonToken.VALUE_STRING) {
            // Nếu client vẫn gửi string, thử parse như enum name (backward compatible)
            String value = p.getText();
            try {
                return Enum.valueOf(enumClass, value);
            } catch (IllegalArgumentException e) {
                throw new IOException("Unknown " + enumClass.getSimpleName() + ": " + value);
            }
        }
        return null;
    }
}

