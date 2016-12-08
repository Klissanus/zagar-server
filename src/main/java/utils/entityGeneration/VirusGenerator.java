package utils.entityGeneration;

import model.Field;
import org.jetbrains.annotations.NotNull;

/**
 * @author apomosov
 */
public abstract class VirusGenerator extends EntityGenerator {
    VirusGenerator(@NotNull Field field) {
        super(field);
    }
}
