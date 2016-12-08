package ticker;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Created by apomosov on 14.05.16.
 */
public interface Tickable {
    void tick(@NotNull Duration elapsed);
}
