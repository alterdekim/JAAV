package com.alterdekim.fridaapp.room;

import androidx.room.TypeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class Converters {
    private static final ObjectMapper mapper = new ObjectMapper();

    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        try {
            return mapper.readValue(value, new TypeReference<ArrayList<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}