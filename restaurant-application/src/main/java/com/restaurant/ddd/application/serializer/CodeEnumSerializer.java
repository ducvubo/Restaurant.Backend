package com.restaurant.ddd.application.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.restaurant.ddd.domain.enums.CodeEnum;

import java.io.IOException;

/**
 * Generic serializer để serialize bất kỳ enum nào implement CodeEnum thành số (code)
 * Không cần tạo serializer riêng cho mỗi enum mới
 */
public class CodeEnumSerializer extends JsonSerializer<CodeEnum> {

    @Override
    public void serialize(CodeEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeNumber(value.code());
        }
    }
}

