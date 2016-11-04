package accountserver.api;

import accountserver.database.Token;
import accountserver.database.TokenDAO;
import accountserver.database.User;
import accountserver.database.UserDAO;
import main.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import utils.JSONDeserializationException;
import utils.JSONHelper;

import javax.ws.rs.*;
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
        if (user == null) {
            log.warn("Not found token " + token + " owner");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        user.setName(newName);
        ApplicationContext.instance().get(UserDAO.class).updateUser(user);
        return Response.ok("Username changed to "+newName).build();

    }

    @POST
    @Authorized
    @Path("changepass")
    public Response changePassword(@Context HttpHeaders headers,
                                   @FormParam("oldpass") String oldpass, @FormParam("newpass") String newpass) {
        if (newpass.equals("")) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        Token token = AuthenticationFilter.getTokenFromHeaders(headers);
        if (token==null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        User user = ApplicationContext.instance().get(TokenDAO.class).getTokenOwner(token);
        if (user==null) {
            log.warn("Not found token " + token + " owner");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        if (!user.validatePassword(oldpass)) {
            log.info("User " + user.getName() + " requested password change with invalid old password");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        log.info("User " + user.getName() + " requested change password");
        user.updatePassword(newpass);
        ApplicationContext.instance().get(UserDAO.class).updateUser(user);
        return Response.ok().build();
    }

    @GET
    @Authorized
    @Path("info")
    public Response profileInfo(@Context HttpHeaders headers) {
        Token token = AuthenticationFilter.getTokenFromHeaders(headers);
        if (token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        User user = ApplicationContext.instance().get(TokenDAO.class).getTokenOwner(token);
        if (user == null) {
            log.warn("Not found token " + token + " owner");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(JSONHelper.toJSON(user)).build();
    }

    @POST
    @Authorized
    @Path("update")
    public Response profileUpdate(@Context HttpHeaders headers, @FormParam("data") String data) {
        Token token = AuthenticationFilter.getTokenFromHeaders(headers);
        if (token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        User user = ApplicationContext.instance().get(TokenDAO.class).getTokenOwner(token);
        if (user == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        log.info("User " + user + " requested profile update");
        User fromData;
        try {
            fromData = JSONHelper.fromJSON(data, User.class);
        } catch (JSONDeserializationException e) {
            log.info("User " + user + " profile update failed");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        user.cloneProfile(fromData);
        ApplicationContext.instance().get(UserDAO.class).updateUser(user);
        return Response.ok("Your profile updated").build();
    }
}
