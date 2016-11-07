package tests;

import accountserver.database.Token;
import accountserver.database.TokenDao;
import accountserver.database.User;
import accountserver.database.UserDao;
import com.squareup.okhttp.Response;
import main.ApplicationContext;
import org.junit.Test;

import static javax.ws.rs.core.Response.Status;
import static org.junit.Assert.*;

/**
 * Created by user on 06.11.16.
 *
 * Authentication API tests
 */
public class TestAuthenticationApi extends WebServerTest {
    @Test
    public void testregister(){
        String[] users = new String[]{null,"","abcd","abcd","uasda"};
        String[] passwords = new String[]{"123","aa","okok","asda",""};
        Status[] expectedStatuses = new Status[]{
                Status.NOT_ACCEPTABLE,
                Status.NOT_ACCEPTABLE,
                Status.OK,
                Status.NOT_ACCEPTABLE,
                Status.NOT_ACCEPTABLE
        };
        final String urlPostfix = "auth/register";
        try {
            for(int i=0;i<users.length;i++){
                Response actual = postRequest(urlPostfix,String.format("user=%s&password=%s",users[i],passwords[i]),null);
                assertEquals(expectedStatuses[i],Status.fromStatusCode(actual.code()));
            }
        } catch (Exception e) {
            fail(e.toString());
        } finally {
            for(String name: users) {
                User u = ApplicationContext.instance()
                        .get(UserDao.class)
                        .getUserByName(name==null ? "null": name);
                if (u!=null)
                    ApplicationContext.instance()
                            .get(UserDao.class)
                            .removeUser(u);
            }
        }
    }

    @Test
    public void testlogin(){
        final String user = "abcd";
        final String password = "151";
        final String loginformat = "user=%s&password=%s";
        final String urlPostfix = "auth/login";

        try{
            Response actual = postRequest(urlPostfix, String.format(loginformat, "", "123"),null);
            assertEquals(Status.NOT_ACCEPTABLE, Status.fromStatusCode(actual.code()));
            actual = postRequest(urlPostfix, String.format(loginformat, user, password),null);
            assertEquals(Status.UNAUTHORIZED, Status.fromStatusCode(actual.code()));
            ApplicationContext.instance().get(UserDao.class).addUser(new User(user, password));
            actual = postRequest(urlPostfix, String.format(loginformat, user, password),null);
            assertEquals(Status.OK, Status.fromStatusCode(actual.code()));
            String rawToken = actual.body().string();
            Token token = ApplicationContext.instance().get(TokenDao.class).findByValue(rawToken);
            assertNotNull(token);
            User u = ApplicationContext.instance().get(TokenDao.class).getTokenOwner(token);
            assertNotNull(u);
            assertEquals(user,u.getName());

            ApplicationContext.instance().get(TokenDao.class).removeToken(token);
            u = ApplicationContext.instance().get(UserDao.class).getUserByName(user);
            if (u!=null) ApplicationContext.instance().get(UserDao.class).removeUser(u);
        }catch(Exception e){
            fail(e.toString());
        }

    }

    @Test
    public void testlogout(){
        final String user = "abcd";
        final String password = "151";
        final String urlPostfix = "auth/logout";
        User u = new User(user, password);
        ApplicationContext.instance().get(UserDao.class).addUser(u);
        try {
            Token token = ApplicationContext.instance().get(TokenDao.class).generateToken(u);
            Response actual = postRequest(urlPostfix,"",token.toString());
            assertEquals(Status.OK,Status.fromStatusCode(actual.code()));
            assertNull(ApplicationContext.instance().get(TokenDao.class).findByValue(token.toString()));
            assertNull(ApplicationContext.instance().get(TokenDao.class).getTokenOwner(token));
            assertNull(ApplicationContext.instance().get(TokenDao.class).getUserToken(u));
        } catch (Exception e) {
            fail(e.toString());
        } finally {
            ApplicationContext.instance().get(TokenDao.class).removeToken(u);
            ApplicationContext.instance().get(UserDao.class).removeUser(u);
        }
    }

}