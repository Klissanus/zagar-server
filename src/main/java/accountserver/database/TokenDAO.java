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
public interface TokenDAO {
    /**
     * Time interval for periodic removing of invalid tokens
     */
    Duration TOKEN_REMOVAL_INTERVAL = Duration.ofHours(2);

    /**
     * Issues a new token (if was not found or invalid) or returns alrady issued
     * @param userId user who wants token
     * @return found or issued token
     */
    @NotNull
    Token generateToken(int userId);

    /**
     * Finds user`s token
     * @param userId user`s ID which token will be searched
     * @return user`s token if it`s found and it is valid, null otherwise
     */
    @Nullable
    Token getUserToken(int userId);

    /**
     * Finds token owner
     * @param token token which owner will be searched
     * @return user id, null if not found
     */
    @Nullable
    Integer getTokenOwner(@NotNull Token token);

    /**
     * Find given raw token in storage
     * @param rawToken token to find
     * @return Token object if it was found and valid, null otherwise
     */
    @Nullable
    Token findByValue(@NotNull String rawToken);

    /**
     *
     * @return list of user id`s with valid tokens
     */
    @NotNull
    List<Integer> getValidTokenOwners();

    /**
     * Removes token from storage
     * @param token token to remove
     */
    void removeToken(@NotNull Token token);

    /**
     * Removes user`s token from storage
     * @param userId user which token will be removed
     */
    void removeToken(int userId);
}
