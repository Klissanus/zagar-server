package model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author apomosov
 */
public class Field {
  private final int width;
  private final int height;
  @NotNull
  private List<Cell> cells = new ArrayList<>();

  public Field() {
    this.width = GameConstants.FIELD_WIDTH;
    this.height = GameConstants.FIELD_HEIGHT;
  }

  @NotNull
  public List<Virus> getViruses() {
    return cells.stream()
            .filter(c -> c instanceof Virus)
            .map(v -> (Virus) v)
            .collect(Collectors.toList());
  }

  @NotNull
  public List<Food> getFoods() {
    return cells.stream()
            .filter(c -> c instanceof Food)
            .map(v -> (Food) v)
            .collect(Collectors.toList());
  }

  @NotNull
  public List<Cell> getCells() {
    return cells;
  }

  public void addCell(@NotNull Cell cell) {
    cells.add(cell);
  }

  public void removeCell(@NotNull Cell cell) {
    cells.remove(cell);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
