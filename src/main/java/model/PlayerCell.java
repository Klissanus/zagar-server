package model;

/**
 * @author apomosov
 */
public class PlayerCell extends Cell {
  private final int id;

  private long lastMovementTime = System.currentTimeMillis();

  public PlayerCell(int id, int x, int y) {
    super(x, y, GameConstants.DEFAULT_PLAYER_CELL_MASS);
    this.id = id;
  }

  public int getId() {
    return id;
  }

  @Override
  public void setX(int x) {
    super.setX(x);
    lastMovementTime = System.currentTimeMillis();
  }

  @Override
  public void setY(int y) {
    super.setY(y);
    lastMovementTime = System.currentTimeMillis();
  }

  long getLastMovementTime() {
    return lastMovementTime;
  }
}
