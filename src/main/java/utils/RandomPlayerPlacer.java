package utils;

import model.GameConstants;
import model.Player;
import model.PlayerCell;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * @author apomosov
 */
public class RandomPlayerPlacer implements PlayerPlacer {
  @NotNull
  private final Player player;

  public RandomPlayerPlacer(@NotNull Player player) {
    this.player = player;
  }

  @Override
  public void place(@NotNull Player player) {
    assert(player.getCells().size() == 1);
    Random random = new Random();
    for (PlayerCell playerCell : player.getCells()) {
      playerCell.setX(playerCell.getRadius() + random.nextInt(GameConstants.FIELD_WIDTH - 2 * playerCell.getRadius()));
      playerCell.setY(playerCell.getRadius() + random.nextInt(GameConstants.FIELD_HEIGHT - 2 * playerCell.getRadius()));
      player.addCell(playerCell);
    }
  }
}
