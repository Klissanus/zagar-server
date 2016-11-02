package accountserver.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Random;

/**
 * Created by xakep666 on 23.10.16.
 *
 * Token is a unique identifier of user
 */
public class Token {
    public static final Duration LIFE_TIME = Duration.ofHours(2);

    private long token;
    /**
     * Generates new random token
     */
    public Token() {
        token = new Random().nextLong();
    }

    private Token(long token) {
        this.token = token;
    }

    /**
     * Create token from string
     * @param rawToken string to parse
     * @return Token object if parse was successful, null otherwise
     */
    @Nullable
    public static Token parse(@NotNull String rawToken) {
        try {
            long token = Long.parseLong(rawToken);
            return new Token(token);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    @Override
    public boolean equals(Object o) {
        return (o==this) || (o instanceof Token)
                && ((Token)o).token==this.token;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(token).hashCode();
    }

    @Override
    public String toString() {return Long.valueOf(token).toString();}
}
