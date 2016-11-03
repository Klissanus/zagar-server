package accountserver.api;

import accountserver.database.Token;
import accountserver.database.TokenDAO;
import accountserver.database.User;
import accountserver.database.UserDAO;
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
                ApplicationContext.instance().get(TokenDAO.class).getTokenOwner(token),newName));
        if (ApplicationContext.instance().get(UserDAO.class).getUserByName(newName)!=null) {
            Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        User user = ApplicationContext.instance().get(TokenDAO.class).getTokenOwner(token);
        if (user==null) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        user.setName(newName);
        ApplicationContext.instance().get(UserDAO.class).updateUser(user);
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
        User user = ApplicationContext.instance().get(TokenDAO.class).getTokenOwner(token);
        if (user==null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        user.updatePassword(newpass);
        ApplicationContext.instance().get(UserDAO.class).updateUser(user);
        return Response.ok().build();
    }
}
