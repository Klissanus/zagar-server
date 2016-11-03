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

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xakep666 on 03.11.16.
 *
 * Stores {@link Token} in database and validates it
 */
public class HibernateTokensStorage implements TokenDAO {
    private static final Logger log = LogManager.getLogger(HibernateUsersStorage.class);

    static {
        log.info("Initialized Hibernate tokens storage");
    }

    private Thread prThread = new Thread(this::periodicRemover);
    public HibernateTokensStorage() {prThread.start();}

    @Entity
    static class StoredToken extends Token {
        @Column(name = "owner_id",nullable = false)
        private int ownerId;

        protected StoredToken(){}
        StoredToken(int owner) {
            this.ownerId = owner;
        }

        int getOwner() {
            return ownerId;
        }
        void setOwner(int owner) {
            this.ownerId = owner;
        }
    }

    @Override
    public @NotNull Token generateToken(int userId) {
        try (Session session = ApplicationContext.instance().get(SessionFactory.class).openSession()) {
            Token foundToken = getUserToken(userId);
            if (foundToken!=null) {
                log.info("Found token for user "+userId+" , returning");
                return foundToken;
            }
            log.info("Valid tokens for user "+userId+" not found, creating new");
            Transaction transaction = session.beginTransaction();
            StoredToken newToken = new StoredToken(userId);
            session.save(newToken);
            transaction.commit();
            return newToken;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public @Nullable Token getUserToken(int userId) {
        try (Session session = ApplicationContext.instance().get(SessionFactory.class).openSession()) {
            log.info("Searching token for user "+userId);
            Query query = session.createQuery("from tokens t where t.owner_id = :id");
            query.setParameter("id",userId);
            List response = query.list();
            if (response==null || !(response.get(0) instanceof StoredToken)) {
                log.error("Error retrieving token for user "+userId);
                return null;
            }
            Token ret = null;
            for(Object t:response) {
                if (((Token)t).isValid()) {
                    ret=(Token)t;
                    break;
                }
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public @Nullable Integer getTokenOwner(@NotNull Token token) {
        try (Session session = ApplicationContext.instance().get(SessionFactory.class).openSession()) {
            log.info("Searching token "+token+" owner");
            Query query = session.createQuery("from tokens t where t.val = :val");
            query.setParameter("val",token.getTokenValue());
            List response = query.list();
            if (response==null || !(response.get(0) instanceof StoredToken)) {
                log.error("Error searching token "+token+" owner");
                return null;
            }
            return ((StoredToken) response.get(0)).getOwner();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public @Nullable Token findByValue(@NotNull String rawToken) {
        try (Session session = ApplicationContext.instance().get(SessionFactory.class).openSession()) {
            log.info("Searching token by value" + rawToken);
            Query query = session.createQuery("from tokens t where t.val = :val");
            query.setParameter("val",Long.parseLong(rawToken));
            List resp = query.list();
            if (resp==null || !(resp.get(0) instanceof StoredToken)) {
                log.error("Error searching token by value"+rawToken);
                return null;
            }
            Token token = (Token)resp.get(0);
            return token.isValid() ? token : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public @NotNull List<Integer> getValidTokenOwners() {
        List<Integer> ret = new ArrayList<>();
        try (Session session = ApplicationContext.instance().get(SessionFactory.class).openSession()) {
            log.info("Getting valid token owners");
            Query query = session.createQuery("from tokens");
            List resp = query.list();
            if (resp==null || !(resp.get(0) instanceof StoredToken)) {
                log.error("Error returning valid token owners");
                return ret;
            }
            for(Object t:resp) {
                if (((StoredToken)t).isValid()) {
                    ret.add(((StoredToken)t).getOwner());
                    break;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void removeToken(@NotNull Token token) {
        try (Session session = ApplicationContext.instance().get(SessionFactory.class).openSession()) {
            log.info("Removing token "+token);
            Query query = session.createQuery("delete from tokens t where t.val = :val");
            query.setParameter("val",token.getTokenValue());
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeToken(int userId) {
        try (Session session = ApplicationContext.instance().get(SessionFactory.class).openSession()) {
            log.info("Removing token by owner "+userId);
            Query query = session.createQuery("delete from tokens t where t.owner_id = :owner_id");
            query.setParameter("owner_id",userId);
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void periodicRemover() {
        log.info("Periodic removing of invalid tokens activated");
        while(!Thread.interrupted()) {
            try (Session session = ApplicationContext.instance().get(SessionFactory.class).openSession()) {
                log.info("Time to remove tokens");
                Query query = session.createQuery("delete from tokens t where " +
                        "DATE_ADD(t.issue_date,INTERVAL ("+Token.LIFE_TIME.toMillis()+")*1000 MICROSECOND)<NOW()");
                query.executeUpdate();
                Thread.sleep(TOKEN_REMOVAL_INTERVAL.toMillis());
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void finalize() throws Throwable{
        super.finalize();
        prThread.interrupt();
    }
}
