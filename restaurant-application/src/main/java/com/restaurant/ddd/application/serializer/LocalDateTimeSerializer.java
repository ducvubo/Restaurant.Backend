package com.restaurant.ddd.application.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Serializer để serialize LocalDateTime thành UTC string với Z
 * Format: "2025-12-15T10:00:00Z"
 * 
 * Lưu ý: LocalDateTime không có timezone, nên giả định nó đã là UTC
 * Khi serialize, format với Z để client biết đây là UTC time
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            // LocalDateTime không có timezone, giả định nó đã là UTC
            // Format với Z để client biết đây là UTC time
            String formatted = value.format(FORMATTER);
            gen.writeString(formatted);
        }
    }
}

