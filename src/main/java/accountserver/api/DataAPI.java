package accountserver.api;

import accountserver.database.TokensStorage;
import accountserver.database.User;
import accountserver.database.UsersStorage;
import main.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import utils.JSONHelper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xakep666 on 13.10.16.
 *
 * Provides REST API for work with users
 */
@Path("/data")
public class DataAPI {
    @NotNull
    private static final Logger log = LogManager.getLogger(DataAPI.class);



    /**
     * Method retrieves logged in users (with valid tokens) and serializes it to jso
     * @return serialized list
     */
    @GET
    @Produces("application/json")
    @Path("users")
    public Response loggedInUsers() {
        log.info("Logged in users list requested");
        List<Integer> userIds = ApplicationContext.instance().get(TokensStorage.class).getValidTokenOwners();
        List<User> users = new ArrayList<>(userIds.size());
        userIds.forEach(id->{
            User u = ApplicationContext.instance().get(UsersStorage.class).getUserById(id);
            if (u!=null) users.add(u);
        });
        return Response.ok(JSONHelper.toJSON(users)).build();
    }
}
