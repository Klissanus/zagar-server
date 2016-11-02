package accountserver.api;

import accountserver.database.Token;
import accountserver.database.TokensStorage;
import accountserver.database.UsersStorage;
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

  @POST
  @Path("register")
  @Consumes("application/x-www-form-urlencoded")
  @Produces("text/plain")
  public Response register(@FormParam("user") String user,
                           @FormParam("password") String password) {

    if (user == null || password == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }

    if (user.equals("") || password.equals("")) {
      return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }

    if (!ApplicationContext.instance().get(UsersStorage.class).register(user,password)) {
      return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }

    log.info("New user '{}' registered", user);
    return Response.ok("User " + user + " registered.").build();
  }

  @POST
  @Path("login")
  @Consumes("application/x-www-form-urlencoded")
  @Produces("text/plain")
  public Response authenticateUser(@FormParam("user") String user,
                                   @FormParam("password") String password) {

    if (user == null || password == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    if (user.equals("") || password.equals("")) {
      return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }
    try {
      // Authenticate the user using the credentials provided
      Token token = ApplicationContext.instance().get(UsersStorage.class).requestToken(user,password);
      if (token == null) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      // Issue a token for the user
      log.info("User '{}' logged in", user);

      // Return the token on the response
      return Response.ok(token.toString()).build();

    } catch (Exception e) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

  public static boolean validateToken(String rawToken) {
    Token token = Token.parse(rawToken);
    if (token==null || !ApplicationContext.instance().get(UsersStorage.class).isValidToken(token)) {
      return false;
    }
    log.info("Correct token from '{}'", ApplicationContext.instance().get(TokensStorage.class).getTokenOwner(token));
    return true;
  }

  @POST
  @Authorized
  @Path("logout")
  @Produces("text/plain")
  public Response logout(@Context HttpHeaders headers) {
    Token token = AuthenticationFilter.getTokenFromHeaders(headers);
    if (token==null)
      return Response.status(Response.Status.UNAUTHORIZED).build();
    ApplicationContext.instance().get(UsersStorage.class).logout(token);
    return Response.ok("Logged out").build();
  }
}