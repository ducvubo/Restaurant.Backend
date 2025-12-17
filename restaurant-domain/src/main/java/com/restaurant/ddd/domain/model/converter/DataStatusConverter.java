package com.restaurant.ddd.domain.model.converter;

import com.restaurant.ddd.domain.enums.DataStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DataStatusConverter implements AttributeConverter<DataStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(DataStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.code();
    }

    @Override
    public DataStatus convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        for (DataStatus status : DataStatus.values()) {
            if (status.code().equals(dbData)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown DataStatus code: " + dbData);
    }
}

