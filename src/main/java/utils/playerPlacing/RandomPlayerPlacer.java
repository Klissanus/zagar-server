package utils.playerPlacing;

import model.Field;
import model.Player;
import model.PlayerCell;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

/**
 * @author apomosov
 */
public class RandomPlayerPlacer implements PlayerPlacer {
  @NotNull
  private final Field field;

    public RandomPlayerPlacer(@NotNull Field field) {
        this.field = field;
  }

  @Override
  public void place(@NotNull Player player) {
      List<PlayerCell> playerCells = player.getCells();
      assert (playerCells.size() == 1); //make sure that placer used only at spawn time
    Random random = new Random();
      for (PlayerCell playerCell : playerCells) {
        playerCell.setX(playerCell.getRadius() + random.nextInt(field.getWidth() - 2 * playerCell.getRadius()));
        playerCell.setY(playerCell.getRadius() + random.nextInt(field.getHeight() - 2 * playerCell.getRadius()));
    }
  }
}
