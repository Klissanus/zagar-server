package accountserver.api;

import accountserver.database.Token;
import accountserver.database.TokenDAO;
import accountserver.database.User;
import accountserver.database.UserDAO;
import main.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("/auth")
public class AuthenticationAPI {
  private static final Logger log = LogManager.getLogger(AuthenticationAPI.class);

  public static boolean validateToken(String rawToken) {
    Token token = ApplicationContext.instance().get(TokenDAO.class).findByValue(rawToken);
    if (token == null) {
      return false;
    }
    log.info("Correct token from '{}'", ApplicationContext.instance().get(TokenDAO.class).getTokenOwner(token));
    return true;
  }

  @POST
  @Path("register")
  @Consumes("application/x-www-form-urlencoded")
  @Produces("text/plain")
  public Response register(@FormParam("user") String username,
                           @FormParam("password") String password) {

    if (username == null || password == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }

    if (username.equals("") || password.equals("") ||
            ApplicationContext.instance().get(UserDAO.class).getUserByName(username)!=null) {
      return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }

    ApplicationContext.instance().get(UserDAO.class).addUser(new User(username,password));

    log.info("New user '{}' registered", username);
    return Response.ok("User " + username + " registered.").build();
  }

  @POST
  @Path("login")
  @Consumes("application/x-www-form-urlencoded")
  @Produces("text/plain")
  public Response authenticateUser(@FormParam("user") String username,
                                   @FormParam("password") String password) {

    if (username == null || password == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    if (username.equals("") || password.equals("")) {
      return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }
    try {
      // Authenticate the user using the credentials provided
      User user = ApplicationContext.instance().get(UserDAO.class).getUserByName(username);
      if (user==null || !user.validatePassword(password)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }

      // Issue a token for the user
      Token token = ApplicationContext.instance().get(TokenDAO.class).generateToken(user);
      log.info("User '{}' logged in", user);

      // Return the token on the response
      return Response.ok(token.toString()).build();

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

  @POST
  @Authorized
  @Path("logout")
  @Produces("text/plain")
  public Response logout(@Context HttpHeaders headers) {
    Token token = AuthenticationFilter.getTokenFromHeaders(headers);
    if (token==null)
      return Response.status(Response.Status.UNAUTHORIZED).build();
    ApplicationContext.instance().get(TokenDAO.class).removeToken(token);
    return Response.ok("Logged out").build();
  }
}