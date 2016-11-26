package accountserver.database.leaderboard;

import accountserver.database.users.User;
import org.jetbrains.annotations.NotNull;
import utils.JSONHelper;
import utils.SortedByValueMap;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xakep666 on 26.11.16.
 * <p>
 * Sends leaderboard from file to client (NOT FOR PRODUCTION!!! ONLY FOR TESTS)
 */
public class SimpleJsonLeaderboard implements LeaderboardDao {
    private static URL jsonFileUrl = SimpleJsonLeaderboard.class
            .getClassLoader()
            .getResource("testleaderboard.json");
    private static Map<User, Integer> users = new HashMap<>();

    static {
        try {
            String fileContent =
                    new String(Files.readAllBytes(Paths.get(jsonFileUrl.getFile())), Charset.defaultCharset());
            users = JSONHelper.fromJSON(fileContent, users.getClass());
            users = SortedByValueMap.sortByValues(users);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void addUser(User user) {

    }

    @Override
    public void removeUser(User user) {

    }

    @Override
    public void updateScore(User user, int scoreToAdd) {

    }

    @NotNull
    @Override
    public Map<User, Integer> getTopUsers(int count) {
        return users;
    }
}
