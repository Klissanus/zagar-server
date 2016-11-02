package accountserver.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

/**
 * Created by xakep666 on 24.10.16.
 *
 * Provides abstraction layer for tokens storage
 * Issues, stores, validates {@link Token}
 */
public interface TokensStorage {
    /**
     * Time interval for periodic removing of invalid tokens
     */
    Duration TOKEN_REMOVAL_INTERVAL = Duration.ofHours(2);

    /**
     * Add token to storage
     * @param user user name
     * @param token generated access token
     * @return true if token was added, false otherwise
     */
    boolean addToken(@NotNull String user, @NotNull Token token);

    /**
     * Finds user`s token
     * @param user user name which token will be searched
     * @return user`s token if it`s found and it is valid, null otherwise
     */
    @Nullable
    Token getUserToken(@NotNull String user);

    /**
     * Finds token owner
     * @param token token which owner will be searched
     * @return user name, null if not found
     */
    @Nullable
    String getTokenOwner(@NotNull Token token);

    /**
     * Validates token
     * @param token token to validate
     * @return true if token valid, false otherwise
     */
    boolean isValidToken(@NotNull Token token);

    /**
     *
     * @return list of user names with valid tokens
     */
    @NotNull
    List<String> getValidTokenOwners();

    /**
     * Removes token from storage
     * @param token token to remove
     */
    void removeToken(@NotNull Token token);

    /**
     * Removes user`s token from storage
     * @param user user which token will be removed
     */
    void removeToken(@NotNull String user);
}
