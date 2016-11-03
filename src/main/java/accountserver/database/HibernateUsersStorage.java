package accountserver.database;

import main.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xakep666 on 03.11.16.
 *
 * Stores {@link User} in database using Hibernate framework
 */
public class HibernateUsersStorage implements UserDAO{
    private static final Logger log = LogManager.getLogger(HibernateUsersStorage.class);

    static {
        log.info("Initialized Hibernate users storage");
    }

    @Override
    public void addUser(@NotNull User user) {
        try (Session session = ApplicationContext.instance().get(SessionFactory.class).openSession()){
            log.info("Adding user "+user+ " to database");
            Transaction transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public @Nullable User getUserById(int id) {
        try (Session session = ApplicationContext.instance().get(SessionFactory.class).openSession()) {
            log.info("Searching user with id "+id);
            Query query = session.createQuery("from users u where u.id = :id");
            query.setParameter("id",id);
            List queryList = query.list();
            if (queryList==null || queryList.isEmpty()) return null;
            return (User)queryList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public @Nullable User getUserByName(@NotNull String name) {
        try (Session session = ApplicationContext.instance().get(SessionFactory.class).openSession()) {
            log.info("Searching user with name "+name);
            Query query = session.createQuery("from users u where u.name = :name");
            query.setParameter("name",name);
            List queryList = query.list();
            if (queryList==null || queryList.isEmpty()) return null;
            return (User)queryList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void removeUser(@NotNull User user) {
        try (Session session = ApplicationContext.instance().get(SessionFactory.class).openSession()) {
            log.info("Removing user "+user);
            Transaction transaction = session.beginTransaction();
            session.delete(user);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public @NotNull List<User> getAllUsers() {
        try (Session session = ApplicationContext.instance().get(SessionFactory.class).openSession()) {
            log.info("Getting all users");
            Query query = session.createQuery("from users");
            List resp = query.list();
            if (resp==null || !(resp.get(0) instanceof User)) {
                log.error("Could not retrieve users");
                return new LinkedList<>();
            }

            @SuppressWarnings("unchecked")
            List<User> ret = (List<User>)resp;
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
    }
}
