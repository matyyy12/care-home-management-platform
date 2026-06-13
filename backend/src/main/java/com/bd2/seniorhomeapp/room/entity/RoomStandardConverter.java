package com.bd2.seniorhomeapp.room.entity;

import java.text.Normalizer;
import java.util.Locale;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RoomStandardConverter implements AttributeConverter<RoomStandard, String> {

    @Override
    public String convertToDatabaseColumn(RoomStandard standard) {
        return standard == null ? null : standard.name();
    }

    @Override
    public RoomStandard convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return switch (normalize(value)) {
            case "BASIC", "STANDARD", "PODSTAWOWY" -> RoomStandard.BASIC;
            case "COMFORT", "KOMFORT" -> RoomStandard.COMFORT;
            case "PREMIUM", "PODWYZSZONY" -> RoomStandard.PREMIUM;
            default -> RoomStandard.valueOf(value.trim().toUpperCase(Locale.ROOT));
        };
    }

    private String normalize(String value) {
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
    }
}
