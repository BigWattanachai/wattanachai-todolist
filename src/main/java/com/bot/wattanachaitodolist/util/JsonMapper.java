package com.bot.wattanachaitodolist.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class JsonMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> Optional<String> toJson(T object) {
        try {
            return Optional.of(mapper.writeValueAsString(object));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> fromJson(String json, Class<T> clazz) {
        try {
            return Optional.of(mapper.readValue(json, clazz));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
