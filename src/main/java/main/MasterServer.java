package main;

import accountserver.AccountServer;
import accountserver.database.HibernateTokensStorage;
import accountserver.database.HibernateUsersStorage;
import accountserver.database.TokenDao;
import accountserver.database.UserDao;
import accountserver.database.leaderboard.JdbcLeaderboardStorage;
import accountserver.database.leaderboard.LeaderboardDao;
import matchmaker.MatchMaker;
import matchmaker.MatchMakerImpl;
import mechanics.Mechanics;
import messageSystem.MessageSystem;
import network.ClientConnectionServer;
import network.ClientConnections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import replication.FullStateReplicator;
import replication.Replicator;
import utils.IDGenerator;
import utils.SequentialIDGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by apomosov on 14.05.16
 *
 * Initializes all services.
 */
public class MasterServer {
  @NotNull
  private final static Logger log = LogManager.getLogger(MasterServer.class);
  @NotNull
  private static final List<Service> services = new ArrayList<>();
  private static MasterServer server = new MasterServer();


  public static void main(@NotNull String[] args) throws ExecutionException, InterruptedException {
    MasterServer.start();
  }

  public static void stop() {
    services.forEach(Service::interrupt);
    services.clear();
    ApplicationContext.instance().clear();
    log.info("MasterServer stopped");
  }

  public static void start() throws ExecutionException, InterruptedException {
    log.info("MasterServer started");
    MessageSystem messageSystem = new MessageSystem();
    ApplicationContext.instance().put(MessageSystem.class, messageSystem);
    ApplicationContext.instance().put(MatchMaker.class, new MatchMakerImpl());
    ApplicationContext.instance().put(ClientConnections.class, new ClientConnections());
    ApplicationContext.instance().put(Replicator.class, new FullStateReplicator());
    ApplicationContext.instance().put(IDGenerator.class, new SequentialIDGenerator());
    ApplicationContext.instance().put(UserDao.class, new HibernateUsersStorage());
    ApplicationContext.instance().put(TokenDao.class, new HibernateTokensStorage());
      ApplicationContext.instance().put(LeaderboardDao.class, new JdbcLeaderboardStorage());

    Mechanics mechanics = new Mechanics();

    messageSystem.registerService(Mechanics.class, mechanics);
    messageSystem.registerService(AccountServer.class, new AccountServer(8080));
    messageSystem.registerService(ClientConnectionServer.class, new ClientConnectionServer(7000));
    messageSystem.getServices().forEach(Service::start);

    for (Service service : services) {
      service.join();
    }
  }
}
