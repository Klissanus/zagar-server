package utils.json;

import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;

/**
 * @author apomosov
 */
public class JSONDeserializationException extends Exception {
    JSONDeserializationException(@NotNull JsonSyntaxException cause) {
        super(cause);
    }
}
