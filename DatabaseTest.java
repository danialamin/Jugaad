import entity.Player;
import database.GameStateDao;
import database.DatabaseConnection;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("Starting Database Test...");

        // 1. Test Singleton Connection
        try {
            System.out.println("Attempting to connect to database...");
            // Note: This will likely fail without actual SQL Server running and correct
            // credentials
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            if (dbConn.getConnection() != null) {
                System.out.println("SUCCESS: Connection instance retrieved.");
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed (Expected if SQL Server is not running): " + e.getMessage());
        }

        // 2. Test DAO with Dummy Data
        GameStateDao dao = new GameStateDao();
        Player dummyPlayer = new Player(1, 3.8, 90, 10, 15, 2, 3);

        System.out.println("Testing saveCurrentGame with dummy player: " + dummyPlayer);
        dao.saveCurrentGame(dummyPlayer);

        System.out.println("Testing loadSavedGame for player ID 1...");
        Player loadedPlayer = dao.loadSavedGame(1);

        if (loadedPlayer != null) {
            System.out.println("SUCCESS: Loaded player: " + loadedPlayer);
        } else {
            System.out.println("FAILURE: Could not load player (Expected if database is not set up).");
        }
    }
}
