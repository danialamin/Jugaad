package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton pattern for Database Connection.
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // Connection parameters (Placeholders)
    private String url = "jdbc:sqlserver://localhost:1433;databaseName=campusFlexDb;encrypt=true;trustServerCertificate=true;";
    private String user = "sa"; // Replace with your SQL Server username
    private String password = "YourPassword123"; // Replace with your SQL Server password

    private DatabaseConnection() throws SQLException {
        try {
            // Load the driver (Optional for newer JDBC)
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection established successfully.");
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
