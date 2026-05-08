package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            if (DbConfig.isWindowsAuth()) {
                this.connection = DriverManager.getConnection(DbConfig.url());
            } else {
                this.connection = DriverManager.getConnection(DbConfig.url(), DbConfig.user(), DbConfig.password());
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server Driver not found.", e);
        }
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        } else if (instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
