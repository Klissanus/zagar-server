package utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * @author apomosov
 */
public class JSONHelper {
    private static @NotNull Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @NotNull
    public static String toJSON(@NotNull Object object) {
        return gson.toJson(object);
    }

    @NotNull
    public static String toJSON(@NotNull Object object, @NotNull Type type) {
        return gson.toJson(object, type);
    }

    @NotNull
    public static <T> T fromJSON(@NotNull String json, @NotNull Class<T> type) throws JSONDeserializationException {
        try {
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            throw new JSONDeserializationException(e);
        }
    }

    @NotNull
    public static <T> T fromJSON(@NotNull String json, @NotNull Type type) throws JSONDeserializationException {
        try {
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            throw new JSONDeserializationException(e);
        }
    }

    public static @NotNull JsonObject getJSONObject(@NotNull String string) {
        return gson.fromJson(string, JsonObject.class);
    }
}
