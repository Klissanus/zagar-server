package utils.playerPlacing;

import model.Player;
import model.PlayerCell;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

/**
 * @author apomosov
 */
public class SimplePlayerPlacer implements PlayerPlacer {
  @Override
  public void place(@NotNull Player player) {
    List<PlayerCell> playerCells = player.getCells();
    assert (playerCells.size() == 1); //make sure that placer used only at spawn time
    Random random = new Random();
    for (PlayerCell playerCell : playerCells) {
      playerCell.setX(5);
      playerCell.setY(5);
    }
  }
}
