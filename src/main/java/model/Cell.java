package model;

import org.jetbrains.annotations.NotNull;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author apomosov
 *
 * Describes game cell
 */
public abstract class Cell {
    @NotNull
    private Point2D coordinate;
    @NotNull
    private Point2D lastMovement;
    private int radius;
    private int mass;

    public Cell(@NotNull Point2D coordinate, int mass) {
        this.coordinate = coordinate;
        this.lastMovement = new Point2D.Double(0,0);
        this.mass = mass;
        updateRadius();
    }

    @NotNull
    public Point2D getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(@NotNull Point2D coordinate) {
        this.lastMovement = new Point2D.Double(
                coordinate.getX()-this.coordinate.getX(),
                coordinate.getY()-this.coordinate.getY()
        );
        this.coordinate = coordinate;
    }

    @NotNull
    public Point2D getLastMovement() {
        return lastMovement;
    }

    public int getRadius() {
        return radius;
    }

    public int getMass() {
        return mass;
    }

    public void setMass(int mass) {
        this.mass = mass;
        updateRadius();
    }

    @NotNull
    public Rectangle2D getBox() {
        return new Rectangle2D.Double(
                coordinate.getX()-radius,
                coordinate.getY()-radius,
                radius*2,
                radius*2
        );
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;

        if (mass != cell.mass) return false;
        return coordinate.equals(cell.coordinate);
    }

    @Override
    public int hashCode() {
        int result = coordinate.hashCode();
        result = 31 * result + mass;
        return result;
    }

    private void updateRadius() {
        this.radius = (int) Math.sqrt(this.mass / Math.PI);
    }
}
