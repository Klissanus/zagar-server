package accountserver.api.auth;

import accountserver.database.Token;
import accountserver.database.TokenDao;
import accountserver.database.User;
import accountserver.database.UserDao;
import accountserver.database.leaderboard.LeaderboardDao;
import main.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("/auth")
public class AuthenticationApi {
  private static final Logger log = LogManager.getLogger(AuthenticationApi.class);

  public static boolean validateToken(String rawToken) {
    Token token = ApplicationContext.instance().get(TokenDao.class).findByValue(rawToken);
    if (token == null) {
      return false;
    }
    log.info("Correct token from '{}'", ApplicationContext.instance().get(TokenDao.class).getTokenOwner(token));
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
            ApplicationContext.instance().get(UserDao.class).getUserByName(username) != null) {
      return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }

    ApplicationContext.instance().get(UserDao.class).addUser(new User(username, password));

    //todo лучше сделать так, что бы AddUser в UserDao возвращал Id тогда этот запрос не нужен
    int userId = ApplicationContext.instance()
            .get(UserDao.class)
            .getUserByName(username).getId();

    //добавляем в таблицу Leaderboard
    ApplicationContext.instance()
            .get(LeaderboardDao.class)
            .addUser(userId);

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
      User user = ApplicationContext.instance().get(UserDao.class).getUserByName(username);
      if (user==null || !user.validatePassword(password)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }

      // Issue a token for the user
      Token token = ApplicationContext.instance().get(TokenDao.class).generateToken(user);
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
    ApplicationContext.instance().get(TokenDao.class).removeToken(token);
    return Response.ok("Logged out").build();
  }
}