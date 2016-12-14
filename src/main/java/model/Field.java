package model;

import net.sf.javaml.core.kdtree.KDTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import utils.EatComparator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author apomosov
 */
public class Field {
  @NotNull
  private static final Logger log = LogManager.getLogger(Field.class);
  @NotNull
  private static final double[] leftTop = new double[]{0,0};
  @NotNull
  private static final double[] rightBottom = new double[]{GameConstants.FIELD_WIDTH, GameConstants.FIELD_HEIGHT};
  @NotNull
  private static final String keyMissing = "KDTree: key missing!";
  private final int width;
  private final int height;
  @NotNull
  private KDTree entities = new KDTree(2);
  @NotNull
  private EatComparator eatComparator = new EatComparator();


  public Field() {
    this.width = GameConstants.FIELD_WIDTH;
    this.height = GameConstants.FIELD_HEIGHT;
  }

  @NotNull
  public <T extends Cell> List<T> getCells(Class<T> identifier) {
    return Arrays.stream(entities.range(leftTop, rightBottom))
            .filter(identifier::isInstance)
            .map(identifier::cast)
            .collect(Collectors.toList());
  }

  @NotNull
  List<PlayerCell> getPlayerCells(@NotNull Player player) {
    return Arrays.stream(entities.range(leftTop, rightBottom))
            .filter(PlayerCell.class::isInstance)
            .map(PlayerCell.class::cast)
            .filter(cell -> player.equals(cell.getOwner()))
            .collect(Collectors.toList());
  }

  public void addCell(@NotNull Cell cell) {
    log.trace("Field:{} Added food to ({}, {})", toString(), cell.getX(), cell.getY());
    entities.insert(new double[]{cell.getX(),cell.getY()},cell);
  }

  public void removeCell(@NotNull Cell cell) {
    try {
      entities.delete(new double[]{cell.getX(), cell.getY()});
    } catch (RuntimeException e) {
      if (e.getMessage().equals(keyMissing)){
        log.warn("Field:{}, Trying to remove non-existing cell ({}, {})", toString(), cell.getX(), cell.getY());
      } else {
        e.printStackTrace();
      }
    }
  }


    public void moveCell(@NotNull Cell cell, int newX, int newY){
        removeCell(cell);
        cell.setX(newX);
        cell.setY(newY);
        addCell(cell);
    }

    public void tryToEat(@NotNull Player player){
        player.getCells().forEach(cell -> {
            //берем 3 ближайших шарика
            //исходим из предположения, что за один тик в радиус шара не попадет больше 3 шариков
            //можно сделать проверку, входит ли 3 в радиус, если да, то взять больше ближайших
            Cell[] candidatesToEat = (Cell[]) entities.nearest(new double[]{cell.getX(), cell.getY()}, 3);
            Arrays.stream(candidatesToEat)
                    .filter(c -> Math.pow(c.getX() - cell.getX(), 2.0)
                            + Math.pow(c.getY() - cell.getY(), 2.0)
                            < Math.pow(cell.getRadius(), 2.0))//is in cell radius
                    .filter( c -> eatComparator.compare(cell, c) == 1)//canEat
                    .forEach(c -> {
                        cell.eat(c);//todo update player score??
                        removeCell(c);
                        if(c instanceof Virus){
                            cell.explode();//todo add and remove cells in kd
                        }
                    });
        });
    }


    public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
