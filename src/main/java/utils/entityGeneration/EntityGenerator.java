package utils.entityGeneration;

import model.Field;
import org.jetbrains.annotations.NotNull;
import ticker.Tickable;

import java.time.Duration;

/**
 * Created by xakep666 on 08.12.16.
 * <p>
 * Base class for entity generators
 */
public abstract class EntityGenerator implements Tickable {
    @NotNull
    private final Field field;

    EntityGenerator(@NotNull Field field) {
        this.field = field;
    }

    @NotNull
    protected Field getField() {
        return field;
    }

    abstract void generate(@NotNull Duration elapsed);

    @Override
    public void tick(@NotNull Duration elapsed) {
        generate(elapsed);
    }
}
