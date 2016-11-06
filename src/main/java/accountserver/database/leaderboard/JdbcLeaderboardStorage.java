package accountserver.database.leaderboard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by Klissan on 06.11.2016.
 * JdbcLeaderboardStorage
 */
public class JdbcLeaderboardStorage
        implements LeaderboardDao
{
    private static final Logger log = LogManager.getLogger(LeaderboardDao.class);

    /*
            UPDATE leaderboard
            SET score = score + add
            WHERE user = userId;
            */
    @Override
    public void addUser(Integer userId) {
        final String query =
                "INSERT INTO leaderboard (user) " +
                        "VALUES (%d);";
        try (Connection con = JdbcDbConnector.getConnection();
             Statement stm = con.createStatement()) {
            stm.execute(String.format(query, userId));
        } catch (SQLException e) {
            log.error("Failed to add user with id {}", userId, e);
        }
    }

    /*
        UPDATE leaderboard
        SET score = score + add
        WHERE user = userId;
        */
    @Override
    public void updateScore(int userId, int scoreToAdd) {
        final String query =
                "UPDATE leaderboard" +
                "SET score = score + %d" +
                "WHERE user = %d;";
        try (Connection con = JdbcDbConnector.getConnection();
             Statement stm = con.createStatement()) {
            stm.execute(String.format(query, scoreToAdd, userId));
        } catch (SQLException e) {
            log.error("Failed to add score {} to user with id {}", scoreToAdd, userId, e);
        }
    }

    /*
    SELECT TOP count * FROM leaderboard
    ORDER BY score
    */
    @Override
    public Map<Integer, Integer> getTopUsers(int count) {
        final String query =
                "SELECT TOP %d * FROM leaderboard" +
                        "ORDER BY score;";

        Map<Integer, Integer> leaders = new HashMap<>();
        try (Connection con = JdbcDbConnector.getConnection();
             Statement stm = con.createStatement()) {
            ResultSet rs = stm.executeQuery(String.format(query, count));

            while (rs.next()) {
                leaders.put(
                        rs.getInt("user"),
                        rs.getInt("score")
                );
            }
            return leaders;
        } catch (SQLException e) {
            log.error("Get leaders failed.",  e);
            return leaders;
        }
    }

}
