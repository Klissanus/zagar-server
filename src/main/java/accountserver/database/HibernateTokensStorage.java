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

    @Entity
    static class StoredToken extends Token {
        @Column(name = "owner_id",nullable = false)
        private int ownerId;

        StoredToken(int owner) {
            this.ownerId = owner;
        }

        public int getOwner() {
            return ownerId;
        }
        public void setOwner(int owner) {
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
            Token ret = (Token)response.get(0);
            return ret.isValid() ? ret : null;
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
        return null;
    }

    @Override
    public void removeToken(@NotNull Token token) {

    }

    @Override
    public void removeToken(int userId) {

    }
}
