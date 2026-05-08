package database;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import state.GameState;

/**
 * HibernateUtil — builds and exposes a singleton SessionFactory configured
 * from {@link DbConfig} (so Windows Authentication keeps working).
 *
 * Schema is auto-generated/updated by Hibernate on first use
 * (hibernate.hbm2ddl.auto = update).
 */
public final class HibernateUtil {

    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() {}

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null) {
                    sessionFactory = build();
                }
            }
        }
        return sessionFactory;
    }

    private static SessionFactory build() {
        Configuration cfg = new Configuration();

        cfg.setProperty("hibernate.connection.driver_class",
                "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        cfg.setProperty("hibernate.connection.url", DbConfig.url());
        cfg.setProperty("hibernate.dialect",
                "org.hibernate.dialect.SQLServerDialect");

        if (!DbConfig.isWindowsAuth()) {
            cfg.setProperty("hibernate.connection.username", DbConfig.user());
            cfg.setProperty("hibernate.connection.password", DbConfig.password());
        }

        cfg.setProperty("hibernate.hbm2ddl.auto", "update");
        cfg.setProperty("hibernate.show_sql", "false");
        cfg.setProperty("hibernate.format_sql", "false");
        // Keep the built-in connection pool small — single-player game.
        cfg.setProperty("hibernate.connection.pool_size", "2");

        cfg.addAnnotatedClass(GameState.class);

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(cfg.getProperties())
                .build();

        try {
            return cfg.buildSessionFactory(registry);
        } catch (RuntimeException e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw e;
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}
