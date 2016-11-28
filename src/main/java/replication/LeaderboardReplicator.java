package replication;

import accountserver.database.leaderboard.LeaderboardDao;
import main.ApplicationContext;
import model.Player;
import network.ClientConnections;
import network.packets.PacketLeaderBoard;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


/**
 * Created by xakep666 on 28.11.16.
 *
 * replicate leaderboard
 */
public class LeaderboardReplicator {
    public void replicate() {
        String[] leaderboard = ApplicationContext.instance().get(LeaderboardDao.class).getTopUsers(10)
                .entrySet()
                .stream()
                .map(entry->entry.getKey().getName())
                .toArray(String[]::new);
        Set<Map.Entry<Player,Session>> sessions = ApplicationContext.instance().get(ClientConnections.class).getConnections();
        sessions.forEach(entry->{
            try {
                if (entry.getValue().isOpen()) {
                    new PacketLeaderBoard(leaderboard).write(entry.getValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
