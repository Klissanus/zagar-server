package accountserver.api;

import accountserver.database.Token;
import accountserver.database.TokensStorage;
import accountserver.database.User;
import accountserver.database.UsersStorage;
import main.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Created by xakep666 on 13.10.16.
 *
 * Provides REST API for work with user profile
 */
@Path("/profile")
public class ProfileAPI {
    @NotNull
    private static final Logger log = LogManager.getLogger(ProfileAPI.class);

    /**
     * Change token owner`s name to given
     * @param newName name to set
     * @return OK if name changed, NOT_ACCEPTABLE otherwise
     */
    @POST
    @Authorized
    @Produces("text/plain")
    @Path("name")
    public Response setNewName(@FormParam("name") String newName,
                               @Context HttpHeaders headers) {
        Token token = AuthenticationFilter.getTokenFromHeaders(headers);
        if (token==null) return Response.status(Response.Status.UNAUTHORIZED).build();
        log.info(String.format("User \"%s\" requested name change to \"%s\"",
                ApplicationContext.instance().get(TokensStorage.class).getTokenOwner(token),newName));
        if (ApplicationContext.instance().get(UsersStorage.class).getUserByName(newName)!=null) {
            Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        Integer tokenOwner = ApplicationContext.instance().get(TokensStorage.class).getTokenOwner(token);
        if (tokenOwner==null) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        User user = ApplicationContext.instance().get(UsersStorage.class).getUserById(tokenOwner);
        if (user==null) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        user.setName(newName);
        return Response.ok("Username changed to "+newName).build();

    }

    @POST
    @Authorized
    @Path("changepass")
    public Response changePassword(@Context HttpHeaders headers, @FormParam("newpass") String newpass) {
        if (newpass.equals("")) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        Token token = AuthenticationFilter.getTokenFromHeaders(headers);
        if (token==null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Integer userId = ApplicationContext.instance().get(TokensStorage.class).getTokenOwner(token);
        if (userId==null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        User user = ApplicationContext.instance().get(UsersStorage.class).getUserById(userId);
        if (user==null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        user.updatePassword(newpass);
        return Response.ok().build();
    }
}
