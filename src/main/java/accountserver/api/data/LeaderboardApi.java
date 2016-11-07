package accountserver.api.data;

import accountserver.database.User;
import accountserver.database.UserDao;
import accountserver.database.leaderboard.LeaderboardDao;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import main.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import utils.JSONHelper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Klissan on 06.11.2016.
 * LeaderboardApi
 */

@Path("/data")
public class LeaderboardApi {

    @NotNull
    private static final Logger log = LogManager.getLogger(LeaderboardApi.class);

    /*Protocol: HTTP
    Path: data/leaderboard
    Method: GET
    Host: {IP}:8080 (IP = localhost при локальном тестрировании сервера)*/
    @GET
    @Produces("application/json")
    @Path("leaderboard")
    public Response getTopUsers(@QueryParam("N") int count){

        log.info("Logged in users list requested");

        //get leaders
        Map<Integer, Integer>  leaders = ApplicationContext
                .instance()
                .get(LeaderboardDao.class)
                .getTopUsers(count);

        //get user info by leaders id
        List<User> users = new ArrayList<>();
        for(Integer id : leaders.keySet()) {
            users.add(
                    ApplicationContext
                            .instance()
                            .get(UserDao.class)
                            .getUserById(id)
            );
        }

        //объединяем имена и очки
        LeaderboardApi.UserInfo ret = new UserInfo();
        int counter = 0;
        for(Integer id : leaders.keySet()) {

            ret.leadersWithScore.put(
                    users.get(counter++).getName(),
                    leaders.get(id)
            );
        }

        //отправляем ответ
        return Response.ok(
                JSONHelper
                .toJSON(ret,
                        new TypeToken<LeaderboardApi.UserInfo>() {}
                        .getType()
                )
        ).build();
    }

    private static class UserInfo {
        @Expose
        Map<String,Integer> leadersWithScore;
        //name + score
    }
}
