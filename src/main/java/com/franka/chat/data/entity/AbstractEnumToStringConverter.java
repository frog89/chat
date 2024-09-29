package com.franka.chat.data.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public abstract class AbstractEnumToStringConverter<T extends Enum<T>, String> implements AttributeConverter<T, String> {
    private final Class<T> clazz;

    public AbstractEnumToStringConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        String dbValue = attribute == null ? null : (String)attribute.name();
        return dbValue;
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        T[] enums = clazz.getEnumConstants();

        for (T e : enums) {
            if (e.name().equals(dbData)) {
                return e;
            }
        }

        throw new UnsupportedOperationException();
    }
}
