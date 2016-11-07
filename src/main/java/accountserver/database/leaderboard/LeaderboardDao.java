package accountserver.database.leaderboard;

import java.util.SortedMap;

/**
 * Created by Klissan on 06.11.2016.
 * interface LeaderboardDao
 */
public interface LeaderboardDao {
    void addUser(Integer userId);
    void removeUser(Integer userId);
    void updateScore(int userId, int scoreToAdd);
    //id, score
    SortedMap<Integer, Integer> getTopUsers(int count);

}
