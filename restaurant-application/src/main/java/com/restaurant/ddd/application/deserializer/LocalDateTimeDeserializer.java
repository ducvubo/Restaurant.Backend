package com.restaurant.ddd.application.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Deserializer để deserialize UTC string với Z thành LocalDateTime
 * Hỗ trợ các format:
 * - "2025-12-15T10:00:00Z" (ISO-8601 với Z)
 * - "2025-12-15T10:00:00.000Z" (ISO-8601 với milliseconds và Z)
 * - "2025-12-15T10:00:00+00:00" (ISO-8601 với timezone offset)
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC),
            DateTimeFormatter.ISO_INSTANT,
            DateTimeFormatter.ISO_DATE_TIME
    };

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.getCurrentToken();

        if (token == JsonToken.VALUE_STRING) {
            String value = p.getText().trim();
            
            if (value.isEmpty()) {
                return null;
            }

            // Thử parse với các format khác nhau
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    if (formatter == DateTimeFormatter.ISO_INSTANT) {
                        // ISO_INSTANT parse thành Instant, sau đó convert sang LocalDateTime
                        Instant instant = Instant.parse(value);
                        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
                    } else if (formatter == DateTimeFormatter.ISO_DATE_TIME) {
                        // ISO_DATE_TIME có thể có timezone, parse và convert về UTC
                        return java.time.ZonedDateTime.parse(value, formatter)
                                .withZoneSameInstant(ZoneOffset.UTC)
                                .toLocalDateTime();
                    } else {
                        // Các format khác parse trực tiếp
                        return LocalDateTime.parse(value, formatter);
                    }
                } catch (DateTimeParseException e) {
                    // Thử format tiếp theo
                    continue;
                }
            }

            // Nếu không parse được với bất kỳ format nào, throw exception
            throw new IOException("Cannot parse LocalDateTime from string: " + value + 
                    ". Expected format: yyyy-MM-dd'T'HH:mm:ss'Z' or ISO-8601 format");
        } else if (token == JsonToken.VALUE_NULL) {
            return null;
        } else {
            throw new IOException("Cannot deserialize LocalDateTime from token: " + token);
        }
    }
}

