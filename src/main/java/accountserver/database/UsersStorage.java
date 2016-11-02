package accountserver.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by xakep666 on 12.10.16.
 *
 * Provides an abstraction layer for databases of {@link User}
 */
public interface UsersStorage {
    /**
     * Generates access token for username (authorization with password)
     * @param username user`s name
     * @param password user`s password
     * @return null if password is invalid or user not registered, access token otherwise
     */
    @Nullable
    Token requestToken(@NotNull String username, @NotNull String password);

    /**
     * Register user on server
     * @param username user`s name
     * @param password user`s password
     * @return true if registration successful, false otherwise
     */
    boolean register(@NotNull String username, @NotNull String password);

    /**
     * Change user password (token must belong to user and user must submit his password)
     * @param token token of user who wants change his pass
     * @param newpwd user`s new password
     * @return true if password changed, false otherwise
     */
    boolean changePassword(@NotNull Token token, @NotNull String newpwd);

    /**
     * Removes access token from valid tokens (logout user)
     * @param token token to remove
     */
    void logout(@NotNull Token token);

    /**
     * Checks if token is valid (exists and not expired)
     * @param token token to check
     * @return true if valid, false otherwise
     */
    boolean isValidToken(@NotNull Token token);

    /**
     * Finds user and creates Player object by user name
     * @param id user id to find
     * @return User object if found and token valid, null otherwise
     */
    @Nullable
    User getUserById(int id);

    /**
     * Finds logged in users (with valid tokens at now)
     * @return list of logged in user names
     */
    @NotNull
    List<String> getLoggedInUsers();

    /**
     * Sets new name to token owner
     * @param newName a new name
     * @param token token of user whose name will be changed
     * @return true if name was changed, false otherwise
     */
    boolean setNewName(@NotNull String newName, @NotNull Token token);
}
