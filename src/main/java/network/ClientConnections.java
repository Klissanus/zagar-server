package network;

import model.Player;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alpi
 * @since 31.10.16
 */
public class ClientConnections {
  private final ConcurrentHashMap<Player, Session> connections = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<Session, Player> players = new ConcurrentHashMap<>();

  public Session registerConnection(Player player, Session session) {
    players.putIfAbsent(session, player);
    return connections.putIfAbsent(player, session);
  }

  public boolean removeConnection(Player player) {
    Session session = connections.get(player);
    return connections.remove(player) != null && players.remove(session) != null;
  }

  public Set<Map.Entry<Player, Session>> getConnections() {
    return connections.entrySet();
  }

  public Player getPlayerBySession(Session session) {
    return players.get(session);
  }
}
