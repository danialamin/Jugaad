package database;

import org.hibernate.Session;
import org.hibernate.Transaction;
import state.GameState;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQL Server persistence handler backed by Hibernate ORM.
 *
 * Responsibilities:
 *   - Bootstrap: create the {@code campusFlexDb} database if missing
 *     (Hibernate can create tables but not databases).
 *   - Delegate save/load to a Hibernate {@link Session} — schema is
 *     created/updated automatically via {@code hibernate.hbm2ddl.auto=update}.
 */
public class SQLServerHandler implements PersistenceHandler {

    private static volatile boolean databaseEnsured = false;

    public SQLServerHandler() {
        ensureDatabaseExists();
    }

    private static synchronized void ensureDatabaseExists() {
        if (databaseEnsured) return;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "SQL Server JDBC Driver not found on classpath.", e);
        }
        String url = DbConfig.url();
        String dbName = "campusFlexDb";
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("databaseName=([^;]+)", java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(url);
        if (m.find()) dbName = m.group(1);
        String masterUrl = url.replaceAll("(?i)databaseName=[^;]+", "databaseName=master");
        try (Connection conn = DbConfig.isWindowsAuth()
                ? DriverManager.getConnection(masterUrl)
                : DriverManager.getConnection(masterUrl, DbConfig.user(), DbConfig.password());
             Statement stmt = conn.createStatement()) {
            stmt.execute("IF DB_ID('" + dbName.replace("'", "''") + "') IS NULL "
                       + "CREATE DATABASE [" + dbName.replace("]", "]]") + "]");
            databaseEnsured = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean save(GameState state) {
        if (state.getTimestamp() == null) {
            state.setTimestamp(new java.util.Date().toString());
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            // merge() handles both insert and update by primary key (saveId).
            session.merge(state);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public GameState load(int saveId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(GameState.class, saveId);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
