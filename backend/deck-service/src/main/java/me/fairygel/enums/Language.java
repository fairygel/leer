package me.fairygel.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {
    EN("en"),
    RU("ru");

    @JsonValue
    private final String code;

    @JsonCreator
    public static Language fromCode(String value) {
        for (Language lang : values()) {
            if (lang.code.equalsIgnoreCase(value) || lang.name().equalsIgnoreCase(value)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("Unsupported language: " + value);
    }
}
