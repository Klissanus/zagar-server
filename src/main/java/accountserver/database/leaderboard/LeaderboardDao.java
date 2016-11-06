package accountserver.database.leaderboard;

import java.util.Map;

/**
 * Created by Klissan on 06.11.2016.
 * interface LeaderboardDao
 */
public interface LeaderboardDao {
    void addUser(Integer userId);
    void updateScore(int userId, int scoreToAdd);
    //id, score
    Map<Integer, Integer> getTopUsers(int count);

}
