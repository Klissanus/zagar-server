package model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author apomosov
 */
public class Field {
  private final int width;
  private final int height;
  @NotNull
  private List<Food> foods = new ArrayList<>();
  @NotNull
  private List<Virus> viruses = new ArrayList<>();

  public Field() {
    this.width = GameConstants.FIELD_WIDTH;
    this.height = GameConstants.FIELD_HEIGHT;
  }

  @NotNull
  public List<Virus> getViruses() {
    return viruses;
  }

  @NotNull
  public List<Food> getFoods() {
    return foods;
  }

  public void addFood(@NotNull Food food) {
    foods.add(food);
  }

  public void addVirus(@NotNull Virus virus) {
    viruses.add(virus);
  }

  public void removeFood(@NotNull Food food) {
    foods.remove(food);
  }

  public void removeVirus(@NotNull Virus virus) {
    viruses.remove(virus);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
