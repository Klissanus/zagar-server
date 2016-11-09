package utils;

import model.Player;
import model.PlayerCell;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * @author apomosov
 */
public class SimplePlayerPlacer implements PlayerPlacer {
  @NotNull
  private final Player player;

  public SimplePlayerPlacer(@NotNull Player player) {
    this.player = player;
  }

  @Override
  public void place(@NotNull Player player) {
    assert(player.getCells().size() == 1);
    Random random = new Random();
    for (PlayerCell playerCell : player.getCells()) {
      playerCell.setX(5);
      playerCell.setY(5);
      player.addCell(playerCell);
    }
  }
}
