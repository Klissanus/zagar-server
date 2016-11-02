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
public class InMemoryTokensStorage implements TokensStorage {
    @NotNull
    private static Logger log = LogManager.getLogger(User.class);

    @NotNull
    private Map<Token, Integer> tokenOwners = new ConcurrentHashMap<>();
    @NotNull
    private Map<Integer, Token> userTokens = new ConcurrentHashMap<>();
    @NotNull
    private Map<Token, Date> tokenTimed = new ConcurrentHashMap<>();

    private Thread prThread;

    public InMemoryTokensStorage() {
        prThread = new Thread(()->periodicRemover());
        log.info("In-memory tokens storage created");
        prThread.start();
    }

    @Override
    public boolean addToken(int userId,@NotNull Token token) {
        if (tokenOwners.containsKey(token) || userTokens.containsKey(userId)) return false;
        tokenOwners.put(token,userId);
        userTokens.put(userId,token);
        tokenTimed.put(token, new Date(new Date().getTime()+Token.LIFE_TIME.toMillis()));
        return false;
    }

    @Override
    @Nullable
    public Token getUserToken(int userId) {
        if (!userTokens.containsKey(userId)) return null;
        return userTokens.get(userId);
    }

    @Override
    @Nullable
    public Integer getTokenOwner(@NotNull Token token) {
        if (!tokenOwners.containsKey(token)) return null;
        Integer owner = tokenOwners.get(token);
        if (owner==null) return null;
        Date expTime = tokenTimed.get(token);
        if (expTime==null || new Date().after(expTime)) return null;
        return owner;
    }

    @Override
    @NotNull
    public List<Integer> getValidTokenOwners() {
        List<Integer> ret = new ArrayList<>(userTokens.size());
        userTokens.forEach((Integer key, Token value) -> {
            if(new Date().before(tokenTimed.get(value))) ret.add(key);
        });
        return ret;
    }

    @Override
    public void removeToken(@NotNull Token token) {
        Integer owner = tokenOwners.get(token);
        if (owner!=null) {
            tokenOwners.remove(token);
            userTokens.remove(owner);
            tokenTimed.remove(token);
        }
    }

    @Override
    public void removeToken(int userId) {
        Token token = userTokens.get(userId);
        if (token!=null) {
            userTokens.remove(userId);
            tokenOwners.remove(token);
            tokenTimed.remove(token);
        }
    }

    @Override
    public boolean isValidToken(@NotNull Token token) {
        Date expDate = tokenTimed.get(token);
        return (expDate!=null) && (new Date().before(expDate));
    }

    public void periodicRemover() {
        try {
            while(true) {
                Set<Integer> invalidTokenOwners = new HashSet<>();
                Set<Token> invalidTokens = new HashSet<>();
                userTokens.forEach((Integer key, Token value) -> {
                    if (new Date().after(tokenTimed.get(value))){
                        invalidTokenOwners.add(key);
                        invalidTokens.add(value);
                    }
                });
                invalidTokenOwners.forEach(o -> userTokens.remove(o));
                invalidTokens.forEach(t -> {
                    tokenOwners.remove(t);
                    tokenTimed.remove(t);
                });
                Thread.sleep(TOKEN_REMOVAL_INTERVAL.toMillis());
            }
        } catch (InterruptedException ignored) {

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
