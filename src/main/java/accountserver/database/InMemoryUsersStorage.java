package accountserver.database;

import main.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xakep666 on 12.10.16.
 *
 * DataBase uses memory to keep user data
 */
public class InMemoryUsersStorage implements UsersStorage {
    @NotNull
    private static Logger log = LogManager.getLogger(InMemoryUsersStorage.class);

    @NotNull
    private TokensStorage ts;

    @NotNull
    private Map<String,User> users = new HashMap<>();

    public InMemoryUsersStorage() {
        this.ts= ApplicationContext.instance().get(TokensStorage.class);
        log.info("Created in-memory users storage");
    }

    public boolean register(@NotNull String username, @NotNull String password){
        if (username.equals("") || password.equals("")) return false;
        User u = new User(username,password);
        if (users.containsKey(username)) return false;
        users.put(username,u);
        log.info("User \""+username+"\" registered");
        return true;
    }

    @Nullable
    @Override
    public Token requestToken(@NotNull String username, @NotNull String password) {
        if (username.equals("") || password.equals("")) return null;
        if (!users.containsKey(username)) return null;
        User user=users.get(username);
        if (!user.validatePassword(password)) return null;
        Token oldToken = ts.getUserToken(username);
        if (oldToken==null || !ts.isValidToken(oldToken)) {
            Token newToken = new Token();
            ts.addToken(username,newToken);
            return newToken;
        }
        return oldToken;
    }

    @Override
    public void logout(@NotNull Token token) {
        ts.removeToken(token);
    }

    @Override
    public boolean changePassword(@NotNull String username,@NotNull String newpwd) {
        if (!users.containsKey(username)) return false;
        User u = users.get(username);
        u.updatePassword(newpwd);
        return true;
    }

    @Override
    @Nullable
    public User getUserByName(@NotNull String name) {
        return users.get(name);
    }

    @Override
    @Nullable
    public String getTokenOwner(@NotNull Token token) {
        return ts.getTokenOwner(token);
    }

    @Override
    public boolean isValidToken(@NotNull Token token) {
        return ts.isValidToken(token);
    }

    @Override
    public boolean setNewName(@NotNull String newName,@NotNull Token token) {
        String userName = ts.getTokenOwner(token);
        if (userName==null) return false;
        if (!users.containsKey(userName) || users.containsKey(newName)) return false;
        if (!ts.isValidToken(token)) return false;
        User u = users.get(userName);
        u.setName(newName);
        users.remove(userName);
        users.put(newName,u);
        ts.removeToken(userName);
        ts.addToken(newName,token);
        return true;
    }

    @Override
    @NotNull
    public List<String> getLoggedInUsers() {
        return ts.getValidTokenOwners();
    }
}
