package database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Loads SQL Server connection settings from database/db.properties.
 * Defaults to Windows Authentication (integratedSecurity=true) so the
 * project works on machines where only Windows auth is configured in SSMS.
 *
 * Lookup order for the properties file:
 *   1. ./database/db.properties (working dir)
 *   2. classpath: /database/db.properties
 *   3. built-in fallback (Windows auth on localhost:1433, db = campusFlexDb)
 */
public final class DbConfig {

    private static final String DEFAULT_URL =
        "jdbc:sqlserver://localhost:1433;databaseName=campusFlexDb;"
        + "encrypt=true;trustServerCertificate=true;integratedSecurity=true";

    private static final Properties PROPS = load();

    private DbConfig() {}

    private static Properties load() {
        Properties p = new Properties();
        // 1. filesystem
        Path fs = Paths.get("database", "db.properties");
        if (Files.exists(fs)) {
            try (InputStream in = Files.newInputStream(fs)) {
                p.load(in);
                return p;
            } catch (IOException ignored) { }
        }
        // 2. classpath
        try (InputStream in = DbConfig.class.getResourceAsStream("/database/db.properties")) {
            if (in != null) {
                p.load(in);
                return p;
            }
        } catch (IOException ignored) { }
        // 3. fallback defaults (Windows auth)
        p.setProperty("db.url", DEFAULT_URL);
        p.setProperty("db.auth", "windows");
        p.setProperty("db.user", "");
        p.setProperty("db.password", "");
        return p;
    }

    public static String url()      { return PROPS.getProperty("db.url",  DEFAULT_URL); }
    public static String authMode() { return PROPS.getProperty("db.auth", "windows").trim().toLowerCase(); }
    public static String user()     { return PROPS.getProperty("db.user", ""); }
    public static String password() { return PROPS.getProperty("db.password", ""); }

    public static boolean isWindowsAuth() {
        return "windows".equals(authMode()) || url().toLowerCase().contains("integratedsecurity=true");
    }
}
