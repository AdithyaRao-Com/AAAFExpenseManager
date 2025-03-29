package com.adithya.aaafexpensemanager.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class GsonListStringConversion {
    public static String listToJson(List<String> stringList) {
        Gson gson = new Gson();
        return gson.toJson(stringList);
    }

    public static List<String> jsonToList(String jsonString) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        return gson.fromJson(jsonString, listType);
    }
}
