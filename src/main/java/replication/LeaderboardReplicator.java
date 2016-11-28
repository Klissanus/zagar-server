package replication;

import accountserver.database.leaderboard.LeaderboardDao;
import accountserver.database.users.User;
import main.ApplicationContext;
import model.Player;
import network.ClientConnections;
import network.packets.PacketLeaderBoard;
import org.eclipse.jetty.websocket.api.Session;
import utils.SortedByValueMap;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Created by xakep666 on 28.11.16.
 *
 * replicate leaderboard
 */
public class LeaderboardReplicator {
    public void replicate() {
        String[] lb =
                ApplicationContext.instance().get(LeaderboardDao.class).getTopUsers(10)
                .keySet()
                .stream()
                .map(User::getName)
                .toArray(String[]::new);
        Set<Map.Entry<Player,Session>> sessions = ApplicationContext.instance().get(ClientConnections.class).getConnections();
        sessions.forEach(entry->{
            try {
                if (entry.getValue().isOpen()) {
                    new PacketLeaderBoard(lb).write(entry.getValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
