package accountserver.database;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.Duration;
import java.util.Date;
import java.util.Random;

/**
 * Created by xakep666 on 23.10.16.
 *
 * Token is a unique identifier of user
 */
@Entity
@Table(name = "tokens")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //нужно для хранения дополнительных полей в одной таблице
public class Token {
    private static final Duration LIFE_TIME = Duration.ofHours(2);

    @Id
    @Column(name = "val",nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long token;
    @Column(name = "issue_date", nullable = false)
    @NotNull
    private Date generationDate = new Date();
    /**
     * Generates new random token
     */
    protected Token() {
        token = new Random().nextLong();
    }


    long getTokenValue() { return token; }

    /**
     * Determine if token is valid
     * @return true if valid, false otherwise
     */
    boolean isValid() {
        return new Date().before(new Date(generationDate.getTime()+LIFE_TIME.toMillis()));
    }

    /**
     * Compare token value with raw string
     * @param rawToken string to compare
     * @return true if equals, false otherwise
     */
    boolean rawEquals(@NotNull String rawToken) {
        return Long.valueOf(token).toString().equals(rawToken);
    }

    @Override
    public boolean equals(Object o) {
        return (o==this) || (o instanceof Token) && ((Token)o).token==this.token;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(token).hashCode();
    }

    @Override
    public String toString() {return Long.valueOf(token).toString();}
}
