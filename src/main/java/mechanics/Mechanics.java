package mechanics;

import main.ApplicationContext;
import main.Service;
import messageSystem.Message;
import messageSystem.MessageSystem;
import messageSystem.messages.LeaderboardMsg;
import messageSystem.messages.ReplicateMsg;
import model.GameConstants;
import model.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import protocol.commands.CommandEjectMass;
import protocol.commands.CommandMove;
import protocol.commands.CommandSplit;
import ticker.Tickable;
import ticker.Ticker;

import java.time.Duration;

/**
 * Created by apomosov on 14.05.16.
 *
 * Game mechanics
 */
public class Mechanics extends Service implements Tickable {
  @NotNull
  private final static Logger log = LogManager.getLogger(Mechanics.class);
  @NotNull
  private final Ticker ticker = new Ticker(this);

  public Mechanics() {
    super("mechanics");
  }

  @Override
  public void run() {
    log.info(getAddress() + " started");
    ticker.loop();
  }

  @Override
  public void tick(@NotNull Duration elapsed) {
    log.info("Start replication");
    MessageSystem messageSystem = ApplicationContext.instance().get(MessageSystem.class);
    Message message = new ReplicateMsg(getAddress());
    Message lbMessage = new LeaderboardMsg(getAddress());
    if (messageSystem == null) return;
    messageSystem.sendMessage(message);
    messageSystem.sendMessage(lbMessage);

    /*System.out.println("Conns " +
            ApplicationContext.instance().get(ClientConnections.class).getConnections());*/
    //execute all messages from queue
    messageSystem.execForService(this);

      log.trace("Mechanics tick() finished");
  }

  public void ejectMass(@NotNull Player player, @NotNull CommandEjectMass commandEjectMass) {
      log.debug("Mass ejected");
  }

  public void move(@NotNull Player player, @NotNull CommandMove commandMove) {
      log.trace("Moving player {}: dx {} dy {}", player, commandMove.getDx(), commandMove.getDy());
      if (Math.abs(commandMove.getDx()) > GameConstants.MAX_COORDINATE_DELTA_MODULE ||
              Math.abs(commandMove.getDy()) > GameConstants.MAX_COORDINATE_DELTA_MODULE) {
          log.info("Player {} may be cheater", player);
          return;
      }
      player.getCells().forEach(cell -> {
          float newX = cell.getX() + commandMove.getDx();
          if (newX + cell.getRadius() / 2 <= player.getField().getWidth() &&
                  newX - cell.getRadius() / 2 >= 0) cell.setX((int) newX);
          float newY = cell.getY() + commandMove.getDy();
          if (newY + cell.getRadius() / 2 <= player.getField().getHeight() &&
                  newX - cell.getRadius() / 2 >= 0) cell.setY((int) newY);
      });
      //TODO handle collisions
  }

  public void split(@NotNull Player player, @NotNull CommandSplit commandSplit) {
    log.info("Split");
  }
}
