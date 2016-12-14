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
  public <T extends Cell> List<T> getCells(Class<T> identifier) {
    return Arrays.stream(entities.range(leftTop,RightBottom))
            .map(Cell.class::cast)
            .filter(identifier::isInstance)
            .map(identifier::cast)
            .collect(Collectors.toList());
  }

  public void addCell(@NotNull Cell cell) {
    log.trace("Added food to ({}, {})",cell.getX(),cell.getY());
    entities.insert(new double[]{cell.getX(),cell.getY()},cell);
  }

  public void removeCell(@NotNull Cell cell) {
    try {
      entities.delete(new double[]{cell.getX(), cell.getY()});
    } catch (RuntimeException e) {
      if (e.getMessage().equals(keyMissing)){
        log.warn("Trying to remove non-existing cell ({}, {})",cell.getX(),cell.getY());
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
