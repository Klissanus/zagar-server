package accountserver.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xakep666 on 24.10.16.
 *
 * Tokens storage based on in-memory data structures
 */
public class InMemoryTokensStorage implements TokenDAO {
    @NotNull
    private static Logger log = LogManager.getLogger(User.class);

    @NotNull
    private Map<Token, Integer> tokenOwners = new ConcurrentHashMap<>();
    @NotNull
    private Map<Integer, Token> userTokens = new ConcurrentHashMap<>();

    private Thread prThread;

    public InMemoryTokensStorage() {
        prThread = new Thread(this::periodicRemover);
        log.info("In-memory tokens storage created");
        prThread.start();
    }

    @Override
    @NotNull
    public Token generateToken(int userId) {
        if (userTokens.containsKey(userId)) {
            Token t = userTokens.get(userId);
            if (t.isValid()) return t;
        }
        Token t = new Token();
        userTokens.put(userId,t);
        tokenOwners.put(t,userId);
        return t;
    }

    @Override
    @Nullable
    public Token getUserToken(int userId) {
        if (!userTokens.containsKey(userId)) {
            return null;
        }
        return userTokens.get(userId);
    }

    @Override
    @Nullable
    public Integer getTokenOwner(@NotNull Token token) {
        if (!tokenOwners.containsKey(token)) return null;
        Integer owner = tokenOwners.get(token);
        if (owner==null) return null;
        if (!token.isValid()) return null;
        return owner;
    }

    @Override
    @NotNull
    public List<Integer> getValidTokenOwners() {
        List<Integer> ret = new ArrayList<>(userTokens.size());
        userTokens.forEach((Integer key, Token value) -> {
            if(value.isValid()) ret.add(key);
        });
        return ret;
    }

    @Override
    public @Nullable Token findByValue(@NotNull String rawToken) {
        for(Token token:userTokens.values()) {
            if (token.rawEquals(rawToken) && token.isValid()) return token;
        }
        return null;
    }

    @Override
    public void removeToken(@NotNull Token token) {
        Integer owner = tokenOwners.get(token);
        if (owner!=null) {
            tokenOwners.remove(token);
            userTokens.remove(owner);
        }
    }

    @Override
    public void removeToken(int userId) {
        Token token = userTokens.get(userId);
        if (token!=null) {
            userTokens.remove(userId);
            tokenOwners.remove(token);
        }
    }

    private void periodicRemover() {
        try {
            while(!Thread.currentThread().isInterrupted()) {
                Set<Integer> invalidTokenOwners = new HashSet<>();
                Set<Token> invalidTokens = new HashSet<>();
                userTokens.forEach((Integer key, Token value) -> {
                    if (value.isValid()){
                        invalidTokenOwners.add(key);
                        invalidTokens.add(value);
                    }
                });
                invalidTokenOwners.forEach(o -> userTokens.remove(o));
                invalidTokens.forEach(tokenOwners::remove);
                Thread.sleep(TOKEN_REMOVAL_INTERVAL.toMillis());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void finalize() {
        try {
            super.finalize();
            prThread.interrupt();
        } catch (Throwable ignored) {
        }
    }
}
