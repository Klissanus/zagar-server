package model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author apomosov
 */
public class PlayerCell extends Cell {
  private final int id;

    private AtomicLong lastMovementTime = new AtomicLong(System.currentTimeMillis());

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
      lastMovementTime.set(System.currentTimeMillis());
  }

  @Override
  public void setY(int y) {
    super.setY(y);
      lastMovementTime.set(System.currentTimeMillis());
  }

  long getLastMovementTime() {
      return lastMovementTime.get();
  }
}
