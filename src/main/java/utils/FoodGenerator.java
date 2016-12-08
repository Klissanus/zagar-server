package utils;

import model.Field;
import org.jetbrains.annotations.NotNull;
import ticker.Tickable;
import ticker.Ticker;

import java.time.Duration;

/**
 * @author xakep666
 *
 * Base class for food generators.
 * generate() method will be called every tick
 */
public abstract class FoodGenerator implements Tickable {
    @NotNull
    private final Field field;
    @NotNull
    private final Ticker ticker = new Ticker(this);

    FoodGenerator(@NotNull Field field) {
        this.field = field;
        ticker.loop();
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
