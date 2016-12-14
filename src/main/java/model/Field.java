package model;

import net.sf.javaml.core.kdtree.KDTree;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import java.util.stream.Collectors;

/**
 * @author apomosov
 */
public class Field {
  @NotNull
  private static final Logger log = LogManager.getLogger(Field.class);
  private final int width;
  private final int height;
  @NotNull
  private static final double[] leftTop = new double[]{0,0};
  @NotNull
  private static final double[] RightBottom = new double[]{GameConstants.FIELD_WIDTH,GameConstants.FIELD_HEIGHT};
  @NotNull
  private KDTree entities = new KDTree(2);
  @NotNull
  private static final String keyMissing = "KDTree: key missing!";


  public Field() {
    this.width = GameConstants.FIELD_WIDTH;
    this.height = GameConstants.FIELD_HEIGHT;
  }

  @NotNull
  public List<Virus> getViruses() {
    return Arrays.stream(entities.range(leftTop,RightBottom))
            .map(Cell.class::cast)
            .filter(Virus.class::isInstance)
            .map(Virus.class::cast)
            .collect(Collectors.toList());
  }

  @NotNull
  public List<Food> getFoods() {
    return Arrays.stream(entities.range(leftTop,RightBottom))
            .map(Cell.class::cast)
            .filter(Food.class::isInstance)
            .map(Food.class::cast)
            .collect(Collectors.toList());
  }

  public void addFood(@NotNull Food food) {
    log.trace("Added food to ({}, {})",food.getX(),food.getY());
    entities.insert(new double[]{food.getX(),food.getY()},food);
  }

  public void addVirus(@NotNull Virus virus) {
    log.trace("Added virus to ({}, {})",virus.getX(),virus.getY());
    entities.insert(new double[]{virus.getX(),virus.getY()},virus);
  }

  public void removeFood(@NotNull Food food) {
    try {
      entities.delete(new double[]{food.getX(), food.getY()});
    } catch (RuntimeException e) {
      if (e.getMessage().equals(keyMissing)){
        log.warn("Trying to remove non-existing food ({}, {})",food.getX(),food.getY());
      } else {
        e.printStackTrace();
      }
    }
  }

  public void removeVirus(@NotNull Virus virus) {
    try {
      entities.delete(new double[]{virus.getX(), virus.getY()});
    } catch (RuntimeException e) {
      if (e.getMessage().equals(keyMissing)){
        log.warn("Trying to remove non-existing virus ({}, {})",virus.getX(),virus.getY());
      } else {
        e.printStackTrace();
      }
    }
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
