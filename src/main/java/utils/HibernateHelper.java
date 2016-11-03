package utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Created by xakep666 on 03.11.16.
 *
 * Helper for Hibernate
 */
public class HibernateHelper {
    private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
    public static Session createSession() {
        return sessionFactory.openSession();
    }
}
