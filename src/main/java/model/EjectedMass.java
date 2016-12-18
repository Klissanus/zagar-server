package model;

import org.jetbrains.annotations.NotNull;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.time.Duration;

/**
 * Created by xakep666 on 18.12.16.
 */
public class EjectedMass extends Cell {
    @NotNull
    private final Point2D dest;
    private double speed;
    private final double acceleration;
    private final double angle;

    public EjectedMass(@NotNull Point2D start,@NotNull Point2D dest,int mass,double initialSpeed, double acceleration) {
        super(start,mass);
        this.speed=initialSpeed;
        this.dest=dest;
        this.acceleration=acceleration;
        this.angle=Math.atan2(dest.getY()-start.getY(),dest.getX()-start.getX());
    }

    public void tickMove(@NotNull Rectangle2D fieldBorder, @NotNull Duration elapsed) {
        Point2D newCoordinate = new Point2D.Double(
                getCoordinate().getX()+elapsed.toMillis()*speed*Math.cos(angle),
                getCoordinate().getY()+elapsed.toMillis()*speed*Math.sin(angle)
        );
        if (!fieldBorder.contains(newCoordinate)) return;
        if (speed>0) speed+=acceleration*elapsed.toMillis();
        if (speed<0) speed=0;
    }
}
