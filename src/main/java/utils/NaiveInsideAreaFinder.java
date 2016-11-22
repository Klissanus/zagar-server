package utils;

import model.Cell;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple search (for each item in list check if it is inside area)
 */
public class NaiveInsideAreaFinder implements InsideAreaFinder {
    @NotNull
    @Override
    public <T extends Cell> List<T> findInArea(@NotNull List<T> cells, int leftX, int rightX, int bottomY, int topY) {
        return cells.stream()
                .filter(cell ->
                        cell.getX() >= leftX && cell.getX() <= rightX && cell.getY() >= bottomY && cell.getY() <= topY)
                .collect(Collectors.toList());
    }
}
