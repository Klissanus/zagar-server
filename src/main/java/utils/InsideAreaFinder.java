package utils;

import model.Cell;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by xakep666 on 16.11.16.
 * Interface to find cells which inside specified area
 */
public interface InsideAreaFinder {
    @NotNull <T extends Cell> List<T> findInArea(@NotNull List<T> cells,
                                                 int leftX, int rightX,
                                                 int bottomY, int topY);
}
