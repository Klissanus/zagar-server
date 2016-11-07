package accountserver.api.data;

import accountserver.database.UserDao;
import accountserver.database.leaderboard.LeaderboardDao;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import main.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import utils.JSONHelper;
import utils.SortedByValueMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.SortedMap;
import java.util.TreeMap;

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
        log.info("Top '{}' users by scores requested",count);

        //get leaders
        SortedMap<Integer, Integer>  leaders = ApplicationContext
                .instance()
                .get(LeaderboardDao.class)
                .getTopUsers(count);

        LeaderboardApi.UserInfo ret = new LeaderboardApi.UserInfo();
        leaders.forEach((Integer id, Integer score)->ret.leadersWithScore.put(
                ApplicationContext.instance().get(UserDao.class).getUserById(id).getName(),
                score
        ));
        ret.leadersWithScore = SortedByValueMap.sortByValues(ret.leadersWithScore);
        //отправляем ответ
        return Response.ok(
                JSONHelper
                .toJSON(ret,
                        new TypeToken<LeaderboardApi.UserInfo>() {}
                        .getType()
                )
        ).build();
    }

    public static class UserInfo {
        @Expose
        public SortedMap<String,Integer> leadersWithScore = new TreeMap<>();
        //name + score
    }
}
