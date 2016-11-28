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
  private static final int widthFactor = 1;
  private static final int heightFactor = 1;
  private final int id;
  @NotNull
  private final List<PlayerCell> cells = new ArrayList<>();
  @NotNull
  private String name;
  private int windowWidth;
  private int windowHeight;

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

  public int getWindowWidth() {
    return windowWidth;
  }

  public void setWindowWidth(int windowWidth) {
    this.windowWidth = windowWidth;
  }

  public int getWindowHeight() {
    return windowHeight;
  }

  public void setWindowHeight(int windowHeight) {
    this.windowHeight = windowHeight;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Player) && (id == ((Player) obj).id);
  }

  @NotNull
  @Override
  public String toString() {
    return "Player{" +
        "name='" + name + '\'' +
        '}';
  }
}
