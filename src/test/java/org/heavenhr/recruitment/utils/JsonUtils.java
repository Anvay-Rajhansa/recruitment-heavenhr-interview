package org.heavenhr.recruitment.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonUtils {
    public static String asJsonString(Object any) {
        try {
            return new ObjectMapper().writeValueAsString(any);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Map parseJsonStringInToMap(String jsonString) {
        try {
            return new ObjectMapper().readValue(jsonString, Map.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static <T> T parseJsonStringInObject(String jsonString, Class<T> obj) {
        try {
            return new ObjectMapper().readValue(jsonString, obj);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
