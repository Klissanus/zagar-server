package model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author apomosov
 */
public class PlayerCell extends Cell {
    private final int id;
    private static final @NotNull Logger log = LogManager.getLogger(PlayerCell.class);
    @NotNull
    private AtomicLong lastMovementTime = new AtomicLong(System.currentTimeMillis());
    @NotNull
    private Player owner;

    public PlayerCell(@NotNull Player owner, int id, @NotNull Point2D coordinate, int mass) {
        super(coordinate, mass);
        this.id = id;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    @Override
    public void setCoordinate(@NotNull Point2D newCoordinate) {
        super.setCoordinate(newCoordinate);
        lastMovementTime.set(System.currentTimeMillis());
    }

    public void eat(Cell cell) {
        this.setMass(this.getMass() + cell.getMass());
        this.getOwner().updateScore(cell.getMass());
        if (cell instanceof PlayerCell){
            ((PlayerCell) cell).getOwner().updateScore( - cell.getMass());
        }
        getOwner().getField().removeCell(cell);
        //todo check remove last cell of another player??
    }

    public void explode() {
        //todo explode
    }

    long getLastMovementTime() {
        return lastMovementTime.get();
    }

    @NotNull
    public Player getOwner() {
        return owner;
    }

    public boolean ejectMass(){
        if( getMass() > GameConstants.MASS_TO_EJECT) {
            getOwner().updateScore(-GameConstants.MASS_TO_EJECT);
            setMass(getMass() - GameConstants.MASS_TO_EJECT);
            EjectedMass ejectedMass = new EjectedMass(
                    new Point2D.Double(getCoordinate().getX()+getLastMovement().getX()/4,
                            getCoordinate().getY()+getLastMovement().getY()/4),
                    new Point2D.Double(getCoordinate().getX()*3,getCoordinate().getY()*3),
                    GameConstants.MASS_TO_EJECT,
                    GameConstants.INITIAL_SPEED,
                    GameConstants.EJECTED_MASS_ACCELERATION
            );
            getOwner().getField().addCell(ejectedMass);
            return true;
        }
        return false;
    }

    public boolean split(){
        if (this.getMass()/getOwner().getCells().size() > GameConstants.MASS_TO_SPLIT){
            int size = getOwner().getCells().size()*2;
            int mass = this.getMass()/size;
            double r = 10*Math.sqrt(mass / Math.PI);
            List<Cell> cells = new ArrayList<>();
            double x =getCoordinate().getX();
            double y = getCoordinate().getY();
            for (int i = 0; i < size; i++) {
                cells.add(new PlayerCell(getOwner(),getId()+i,
                        new Point2D.Double(x+i*6*mass,
                        y), mass));
            }
            getOwner().getCells().forEach(c->getOwner().getField().removeCell(c));
            cells.forEach(c -> getOwner().getField().addCell(c));
            log.info("I am true");
            return true;
        }
        log.info("I am false");
        return  false;
    }
}
