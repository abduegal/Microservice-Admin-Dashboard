package com.aegal.framework.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Converts JSON to objects and objects to JSON.
 * Using the same objectmapper as DW.
 * User: A.Egal
 * Date: 8/8/14
 * Time: 7:55 PM
 */
public class JsonConverter {

    private static ObjectMapper objectMapper;

    public static <T> T toObject(String json, Class<T> clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }

    public static <T> T toObject(InputStream json, Class<T> clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }

    public static <T> T toObject(InputStream json, TypeReference type) throws IOException {
        return objectMapper.readValue(json, type);
    }

    public static <T> String fromObject(T object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        JsonConverter.objectMapper = objectMapper;
    }

}
