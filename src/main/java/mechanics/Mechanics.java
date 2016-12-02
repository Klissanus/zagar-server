package mechanics;

import main.ApplicationContext;
import main.Service;
import messageSystem.Message;
import messageSystem.MessageSystem;
import messageSystem.messages.LeaderboardMsg;
import messageSystem.messages.ReplicateMsg;
import model.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import protocol.commands.CommandEjectMass;
import protocol.commands.CommandMove;
import protocol.commands.CommandSplit;
import ticker.Tickable;
import ticker.Ticker;

/**
 * Created by apomosov on 14.05.16.
 *
 * Game mechanics
 */
public class Mechanics extends Service implements Tickable {
  @NotNull
  private final static Logger log = LogManager.getLogger(Mechanics.class);
  @NotNull
  private final Ticker ticker = new Ticker(this, 1);

  public Mechanics() {
    super("mechanics");
  }

  @Override
  public void run() {
    log.info(getAddress() + " started");
    ticker.loop();
  }

  @Override
  public void tick(long elapsedNanos) {
    log.info("Mechanics tick() started");
    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      log.error(e);
      Thread.currentThread().interrupt();
      e.printStackTrace();
    }

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

    log.info("Mechanics tick() finished");
  }

  public void ejectMass(@NotNull Player player, @NotNull CommandEjectMass commandEjectMass) {
    log.info("Mass ejected");
  }

  public void move(@NotNull Player player, @NotNull CommandMove commandMove) {
    log.info("Moved");
  }

  public void split(@NotNull Player player, @NotNull CommandSplit commandSplit) {
    log.info("Split");
  }
}
