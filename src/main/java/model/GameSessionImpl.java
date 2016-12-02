package model;

import org.jetbrains.annotations.NotNull;
import utils.FoodGenerator;
import utils.PlayerPlacer;
import utils.VirusGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author apomosov
 */
public class GameSessionImpl implements GameSession {
  @NotNull
  private final Field field = new Field();
  @NotNull
  private final List<Player> players = new ArrayList<>();
  @NotNull
  private final FoodGenerator foodGenerator;
  @NotNull
  private final PlayerPlacer playerPlacer;
  @NotNull
  private final VirusGenerator virusGenerator;
  @NotNull
  private final Thread afkRemoverThread = new Thread(this::periodicAfkRemover);

  public GameSessionImpl(@NotNull FoodGenerator foodGenerator, @NotNull PlayerPlacer playerPlacer, @NotNull VirusGenerator virusGenerator) {
    this.foodGenerator = foodGenerator;
    this.playerPlacer = playerPlacer;
    this.virusGenerator = virusGenerator;
    virusGenerator.generate();
    afkRemoverThread.start();
  }

  @Override
  public void join(@NotNull Player player) {
    players.add(player);
    this.playerPlacer.place(player);
  }

  @Override
  public void leave(@NotNull Player player) {
    players.remove(player);
  }

  @Override
  @NotNull
  public List<Player> getPlayers() {
    return new ArrayList<>(players);
  }

  private void periodicAfkRemover() {
    try {
      while (!Thread.interrupted()) {
        Thread.sleep(GameConstants.MOVEMENT_TIMEOUT.toMillis() / 2);
        players.removeIf(p -> p.getMinTimeWithoutMovements().compareTo(GameConstants.MOVEMENT_TIMEOUT) > 0);
      }
    } catch (InterruptedException ignored) {

    }
  }

  @NotNull
  @Override
  public Map<Player, Integer> getTop(int n) {
    return players.stream()
            .sorted((player1, player2) -> player2.getTotalScore() - player1.getTotalScore())
            .limit(n)
            .collect(Collectors.toMap(player -> player, Player::getTotalScore));
  }

  @Override
  public @NotNull Field getField() {
    return field;
  }

  @Override
  public void finalize() throws Throwable {
    afkRemoverThread.interrupt();
    super.finalize();
  }

}
