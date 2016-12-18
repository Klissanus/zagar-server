package model;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author apomosov
 */
public class PlayerCell extends Cell {
    private final int id;

    @NotNull
    private AtomicLong lastMovementTime = new AtomicLong(System.currentTimeMillis());
    @NotNull
    private Player owner;

    public PlayerCell(@NotNull Player owner, int id, int x, int y) {
        super(x, y, GameConstants.DEFAULT_PLAYER_CELL_MASS);
        this.id = id;
        this.owner = owner;
    }

    public PlayerCell(@NotNull Player owner, int id, int x, int y, int mass) {
        super(x, y,  mass);
        this.id = id;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        lastMovementTime.set(System.currentTimeMillis());
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        lastMovementTime.set(System.currentTimeMillis());
    }

    public void eat(Cell cell) {
        this.setMass(this.getMass() + cell.getMass());
        this.getOwner().updateScore(cell.getMass());
        if (cell instanceof PlayerCell){
            ((PlayerCell) cell).getOwner().updateScore( - cell.getMass());
        }
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
        if( this.getMass() > GameConstants.MASS_TO_EJECT) {
            this.getOwner().updateScore(-GameConstants.FOOD_MASS);
            this.setMass(this.getMass() - GameConstants.FOOD_MASS);
            return true;
        }
        return false;
    }

    public boolean split(){
        if (this.getMass() > GameConstants.MASS_TO_SPLIT ){
            this.setMass(this.getMass() / 2);
            return true;
        }
        return  false;
    }
}
