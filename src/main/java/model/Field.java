package model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import utils.CellQuadTree;
import utils.EatComparator;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author apomosov
 */
public class Field {
  @NotNull
  private static final Logger log = LogManager.getLogger(Field.class);
    private static final int width = GameConstants.FIELD_WIDTH;
    private static final int height = GameConstants.FIELD_HEIGHT;
  @NotNull
  private static final Rectangle fieldRange = new Rectangle(0, 0, width, height);
  @NotNull
  private final CellQuadTree entities = new CellQuadTree(fieldRange, 128, 12);
  @NotNull
  private EatComparator eatComparator = new EatComparator();


  public Field() {
  }

  @NotNull
  public <T extends Cell> List<T> getCells(Class<T> identifier) {
      return entities.query(fieldRange, identifier::isInstance).stream()
            .map(identifier::cast)
            .collect(Collectors.toList());
  }

  @NotNull
  List<PlayerCell> getPlayerCells(@NotNull Player player) {
      return entities.query(fieldRange,
              c -> (c instanceof PlayerCell) && ((PlayerCell) c).getOwner().equals(player))
              .stream()
            .map(PlayerCell.class::cast)
            .collect(Collectors.toList());
  }

  public void addCell(@NotNull Cell cell) {
      log.trace("Field:{} Added {} to ({}, {})", toString(), cell.getClass().getName(), cell.getX(), cell.getY());
      entities.add(cell);
  }

  public void removeCell(@NotNull Cell cell) {
      entities.remove(cell);
      log.trace("Removing {} from ({}, {})", cell.getClass().getName(), cell.getX(), cell.getY());
  }


    public void moveCell(@NotNull Cell cell, int newX, int newY){
        removeCell(cell);
        cell.setX(newX);
        cell.setY(newY);
        addCell(cell);
    }

    public void tryToEat(@NotNull Player player){
        /*player.getCells().forEach(cell -> {
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
        });*/
    }


    public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
