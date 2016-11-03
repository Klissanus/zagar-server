package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Created by xakep666 on 03.11.16.
 *
 * Helper for Hibernate
 */
public class HibernateHelper {
    private static final SessionFactory sessionFactory;
    private static final Logger log = LogManager.getLogger(HibernateHelper.class);

    static {
        sessionFactory = new Configuration().configure()
                .buildSessionFactory();

        log.info("Session factory configured.");
    }

    private HibernateHelper() {
    }

    private static Session createSession() {
        return sessionFactory.openSession();
    }

    public static <T> List<T> selectTransactional(Function<Session, List<T>> selectAction) {
        Transaction txn = null;
        List<T> ts = Collections.emptyList();
        try (Session session = HibernateHelper.createSession()) {
            txn = session.beginTransaction();
            ts = selectAction.apply(session);
            txn.commit();
        } catch (RuntimeException e) {
            log.error("Transaction failed.", e);
            if (txn != null) {
                txn.rollback();
            }
        }

        return ts;
    }

    public static void doTransactional(Function<Session, ?> f) {
        Transaction txn = null;
        try (Session session = HibernateHelper.createSession()) {
            txn = session.beginTransaction();
            f.apply(session);
            txn.commit();
        } catch (RuntimeException e) {
            log.error("Transaction failed.", e);
            if (txn != null) {
                txn.rollback();
            }
        }
    }
}
