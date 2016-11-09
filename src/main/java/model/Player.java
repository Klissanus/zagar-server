package model;

import main.ApplicationContext;
import org.jetbrains.annotations.NotNull;
import utils.IDGenerator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author apomosov
 */
public class Player {
  private final int id;
  @NotNull
  private final List<PlayerCell> cells = new ArrayList<>();
  @NotNull
  private String name;

  public Player(int id, @NotNull String name) {
    this.id = id;
    this.name = name;
    addCell(new PlayerCell(ApplicationContext.instance().get(IDGenerator.class).next(), 0, 0));
  }

  public void addCell(@NotNull PlayerCell cell) {
    cells.add(cell);
  }

  public void removeCell(@NotNull PlayerCell cell) {
    cells.remove(cell);
  }

  @NotNull
  public String getName() {
    return name;
  }

  public void setName(@NotNull String name) {
    this.name = name;
  }

  @NotNull
  public List<PlayerCell> getCells() {
    return cells;
  }

  public int getId() {
    return id;
  }

  Duration getMinTimeWithoutMovements() {
    Optional<PlayerCell> lastmoved = cells.stream()
            .min((c1, c2) -> Long.compare(c1.getLastMovementTime(), c2.getLastMovementTime()));
    if (!lastmoved.isPresent()) return Duration.ZERO;
    return Duration.ofMillis(lastmoved.get().getLastMovementTime());
  }

  @NotNull
  @Override
  public String toString() {
    return "Player{" +
        "name='" + name + '\'' +
        '}';
  }
}
