package model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import utils.EatComparator;
import utils.quadTree.QuadTree;
import utils.quadTree.TreePoint;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
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
    private final QuadTree<Cell> entities = new QuadTree<>(fieldRange);
    @NotNull
    private EatComparator eatComparator = new EatComparator();


    public Field() {
    }

    @NotNull
    public <T extends Cell> List<T> getCells(Class<T> identifier) {
        return entities.getAllPointsWhere(point->
                point.getItem().isPresent() &&
                        identifier.isInstance(point.getItem().get())
        )
                .stream()
                .map(point->point.getItem().get())
                .map(identifier::cast)
                .collect(Collectors.toList());
    }

    @NotNull
    List<PlayerCell> getPlayerCells(@NotNull Player player) {
        return entities.getAllPointsWhere(point->
                point.getItem().isPresent() &&
                        PlayerCell.class.isInstance(point.getItem().get()) &&
                        PlayerCell.class.cast(point.getItem().get()).getOwner().equals(player)
        )
                .stream()
                .map(point->point.getItem().get())
                .map(PlayerCell.class::cast)
                .collect(Collectors.toList());
    }

    public void addCell(@NotNull Cell cell) {
        log.trace("Field:{} Added {} to ({}, {})", toString(), cell.getClass().getName(), cell.getX(), cell.getY());
        entities.set(new Point2D.Double(cell.getX(),cell.getY()),cell);
    }

    public void removeCell(@NotNull Cell cell) {
        log.trace("Removing {} from ({}, {})", cell.getClass().getName(), cell.getX(), cell.getY());
        entities.remove(new Point2D.Double(cell.getX(),cell.getY()));
    }


    public void moveCell(@NotNull Cell cell, int newX, int newY) {
        removeCell(cell);
        cell.setX(newX);
        cell.setY(newY);
        addCell(cell);
    }

    public void tryToEat(@NotNull Player player) {
        player.getCells().forEach(cell -> {
            int upperLeftX = cell.getX() - cell.getRadius();
            int upperLeftY = cell.getY() - cell.getRadius();
            int diameter = 2 * cell.getRadius();
            Rectangle2D rect = new Rectangle(upperLeftX, upperLeftY, diameter, diameter);
            List<TreePoint<Cell>> candidatesToEat = entities.searchWithin(rect);
            candidatesToEat.stream()
                .filter(c -> Math.pow(c.getCoordinate().getX() - cell.getX(), 2.0)
                + Math.pow(c.getCoordinate().getY() - cell.getY(), 2.0)
                < Math.pow(cell.getRadius(), 2.0))//is in cell radius
                .filter( c -> eatComparator.compare(cell, c.getItem().get()) == 1)//canEat
                .forEach(c -> {
                    Cell anotherCell = c.getItem().get();
                    cell.eat(anotherCell);//todo update player score??
                    removeCell(anotherCell);
                    if(anotherCell instanceof Virus){
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
