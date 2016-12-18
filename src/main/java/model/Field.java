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
import java.util.List;
import java.util.Optional;
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
        log.trace("Field:{} Added {} to ({}, {})",
                toString(), cell.getClass().getName(), cell.getCoordinate().getX(), cell.getCoordinate().getY());
        entities.set(new Point2D.Double(cell.getCoordinate().getX(),cell.getCoordinate().getY()),cell);
    }

    public void removeCell(@NotNull Cell cell) {
        log.trace("Removing {} from ({}, {})",
                cell.getClass().getName(), cell.getCoordinate().getX(), cell.getCoordinate().getY());
        entities.remove(new Point2D.Double(cell.getCoordinate().getX(),cell.getCoordinate().getY()));
    }


    public void moveCell(@NotNull Cell cell, @NotNull Point2D newCoordinate) {
        removeCell(cell);
        cell.setCoordinate(newCoordinate);
        addCell(cell);
    }

    @NotNull
    public List<Cell> findIntersected(@NotNull Cell cell) {
        return entities.searchWithin(cell.getBox()).stream()
                .map(TreePoint::getItem)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @NotNull
    public Rectangle2D getRegion(){
        return fieldRange;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
