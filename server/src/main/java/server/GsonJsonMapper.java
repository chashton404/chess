package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.javalin.json.JsonMapper;
import java.lang.reflect.Type;

public class GsonJsonMapper implements JsonMapper {
    // Create our new gson object
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    // These both accept Thpe becaue its a broad java interface that can accept any type
    // This converts a java object into a Json string when Javalin sends a response
    @Override
    public String toJsonString(Object obj, Type type) {
        return gson.toJson(obj, type);
    }

    // This converts an incoming JSON string back into a java object when they receive their request
    @Override
    public <T> T fromJsonString(String json, Type targetType) {
        return gson.fromJson(json, targetType);
    }
}

