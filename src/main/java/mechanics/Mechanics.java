package mechanics;

import main.ApplicationContext;
import main.Service;
import messageSystem.Message;
import messageSystem.MessageSystem;
import messageSystem.messages.LeaderboardMsg;
import messageSystem.messages.ReplicateMsg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import ticker.Tickable;

/**
 * Created by apomosov on 14.05.16.
 */
public class Mechanics extends Service implements Tickable {
  @NotNull
  private final static Logger log = LogManager.getLogger(Mechanics.class);

  public Mechanics() {
    super("mechanics");
  }

  @Override
  public synchronized void start() {
    super.start();
    log.info(getName() + " started");
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
    Message lbMesage = new LeaderboardMsg(getAddress());
    if (messageSystem == null) return;
    messageSystem.sendMessage(message);
    messageSystem.sendMessage(lbMesage);

    //execute all messages from queue
    messageSystem.execForService(this);


    log.info("Mechanics tick() finished");
  }

  public void ejectMass(){
    log.info("Mass ejected");
  }

  public void move(){
    log.info("Moved");
  }

  public void split(){
    log.info("Split");
  }
}
