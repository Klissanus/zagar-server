package accountserver.api;

import accountserver.database.Token;
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
                ApplicationContext.instance().get(UsersStorage.class).getTokenOwner(token),newName));
        return ApplicationContext.instance().get(UsersStorage.class).setNewName(newName,token) ?
                Response.ok("Username changed to "+newName).build() :
                Response.status(Response.Status.NOT_ACCEPTABLE).build();
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
        String username = ApplicationContext.instance().get(UsersStorage.class).getTokenOwner(token);
        if (username==null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return ApplicationContext.instance().get(UsersStorage.class).changePassword(username,newpass) ?
                Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}
