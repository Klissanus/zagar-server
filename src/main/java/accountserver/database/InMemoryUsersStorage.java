package accountserver.database;

import main.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
        Token oldToken = ts.getUserToken(user.getId());
        if (oldToken==null || !ts.isValidToken(oldToken)) {
            Token newToken = new Token();
            ts.addToken(user.getId(),newToken);
            return newToken;
        }
        return oldToken;
    }

    @Override
    public void logout(@NotNull Token token) {
        ts.removeToken(token);
    }

    @Override
    public boolean changePassword(@NotNull Token token,@NotNull String newpwd) {
        Integer id = ts.getTokenOwner(token);
        if(id==null) return false;
        User u = getUserById(id);
        if (u==null) return false;
        u.updatePassword(newpwd);
        return true;
    }

    @Override
    @Nullable
    public User getUserById(int id) {
        User ret = null;
        for(User u:users.values()) {
            if (u.getId()==id) ret=u;
        }
        return ret;
    }

    @Override
    public boolean isValidToken(@NotNull Token token) {
        return ts.isValidToken(token);
    }

    @Override
    public boolean setNewName(@NotNull String newName,@NotNull Token token) {
        Integer userId = ts.getTokenOwner(token);
        if (userId==null) return false;
        User foundUser = null;
        for (User u: users.values()) {
            if (u.getId()==userId) {
                foundUser=u;
                break;
            }
        }
        if (foundUser==null) return false;
        if (!ts.isValidToken(token) || users.containsKey(newName)) return false;
        foundUser.setName(newName);
        return true;
    }

    @Override
    @NotNull
    public List<String> getLoggedInUsers() {
        List<String> names = new ArrayList<>(users.size());
        List<Integer> ids = ts.getValidTokenOwners();
        users.forEach((String name, User u)->{
            ids.forEach((Integer id)->{
                if(u.getId()==id) names.add(u.getName());
            });
        });
        return names;
    }
}
