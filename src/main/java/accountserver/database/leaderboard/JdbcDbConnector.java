package accountserver.database.leaderboard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


class JdbcDbConnector {
    private static final Logger log = LogManager.getLogger(JdbcDbConnector.class);

    private static final String URL = System.getProperty("hibernate.connection.url");
    private static final String USER = System.getProperty("hibernate.connection.username");
    private static final String PASSWORD = System.getProperty("hibernate.connection.password");
    private static final String DRIVER = System.getProperty("hibernate.connection.driver_class");
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            log.error("Failed to load jdbc driver.", e);
            System.exit(-1);
        }

        log.info("Success. DbConnector init.");
    }

    private JdbcDbConnector() {
    }

    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
