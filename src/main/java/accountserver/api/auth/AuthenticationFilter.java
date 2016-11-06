package accountserver.api.auth;

import accountserver.database.Token;
import accountserver.database.TokenDAO;
import main.ApplicationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

@Authorized
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {
  @Override
  public void filter(@NotNull ContainerRequestContext requestContext) throws IOException {

    // Get the HTTP Authorization header from the request
    String authorizationHeader =
        requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

    // Check if the HTTP Authorization header is present and formatted correctly
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new NotAuthorizedException("Authorization header must be provided");
    }

    // Extract the token from the HTTP Authorization header
    String token = authorizationHeader.substring("Bearer".length()).trim();

    if (!validateToken(token)) {
      requestContext.abortWith(
          Response.status(Response.Status.UNAUTHORIZED).build());
    }
  }

  private boolean validateToken(@NotNull String token) {
    return AuthenticationAPI.validateToken(token);
  }

  @Nullable
  public static Token getTokenFromHeaders(@NotNull HttpHeaders headers) {
    List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
    String rawToken = authHeaders.get(0).substring("Bearer".length()).trim();
    return ApplicationContext.instance().get(TokenDAO.class).findByValue(rawToken);
  }
}