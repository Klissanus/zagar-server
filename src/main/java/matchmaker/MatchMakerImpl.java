package matchmaker;

import main.ApplicationContext;
import model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import ticker.Ticker;
import utils.RandomPlayerPlacer;
import utils.RandomVirusGenerator;
import utils.UniformFoodGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates {@link GameSession} for single player
 *
 * @author Alpi
 */
public class MatchMakerImpl implements MatchMaker {
  @NotNull
  private final Logger log = LogManager.getLogger(MatchMakerImpl.class);
  @NotNull
  private final List<GameSession> activeGameSessions = new ArrayList<>();

  /**
   * Creates new GameSession
   *
   * @param player single player
   */
  @Override
  public void joinGame(@NotNull Player player) {
    //try to find session with free slots
    List<GameSession> withFreeSlots = activeGameSessions.stream()
            .filter(s -> s.getPlayers().size() < GameConstants.MAX_PLAYERS_IN_SESSION)
            .collect(Collectors.toList());
    if (withFreeSlots.size() != 0) {
      withFreeSlots.get(0).join(player);
      log.info("{} joined to session with free slots {}", player, withFreeSlots.get(0));
      return;
    }
    GameSession newGameSession = createNewGame();
    activeGameSessions.add(newGameSession);
    newGameSession.join(player);
    log.info("{} joined {}", player, newGameSession);
  }

  @NotNull
  public List<GameSession> getActiveGameSessions() {
    return new ArrayList<>(activeGameSessions);
  }

  /**
   * @return new GameSession
   */
  private
  @NotNull
  GameSession createNewGame() {
    Field field = new Field();
    Ticker ticker = ApplicationContext.instance().get(Ticker.class);
    UniformFoodGenerator foodGenerator = new UniformFoodGenerator(field, GameConstants.FOOD_PER_SECOND_GENERATION, GameConstants.MAX_FOOD_ON_FIELD);
    ticker.registerTickable(foodGenerator);
    return new GameSessionImpl(foodGenerator, new RandomPlayerPlacer(field), new RandomVirusGenerator(field, GameConstants.NUMBER_OF_VIRUSES));
  }
}
