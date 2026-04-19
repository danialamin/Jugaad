package database;

import engine.Player;
import java.sql.*;

public class GameStateDao {

    public void saveCurrentGame(Player player) {
        String query = "UPDATE playerStats SET gpa = ?, energy = ?, stress = ?, karma = ?, xLocation = ?, yLocation = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, player.getGpa());
            pstmt.setInt(2, player.getEnergy());
            pstmt.setInt(3, player.getStress());
            pstmt.setInt(4, player.getKarma());
            pstmt.setInt(5, player.getXLocation());
            pstmt.setInt(6, player.getYLocation());
            pstmt.setInt(7, player.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                // If update fails, try insert (for first save)
                String insertQuery = "INSERT INTO playerStats (id, gpa, energy, stress, karma, xLocation, yLocation) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, player.getId());
                    insertStmt.setDouble(2, player.getGpa());
                    insertStmt.setInt(3, player.getEnergy());
                    insertStmt.setInt(4, player.getStress());
                    insertStmt.setInt(5, player.getKarma());
                    insertStmt.setInt(6, player.getXLocation());
                    insertStmt.setInt(7, player.getYLocation());
                    insertStmt.executeUpdate();
                    System.out.println("First-time save: Player " + player.getId() + " inserted.");
                }
            } else {
                System.out.println("Game saved for player ID: " + player.getId());
            }

        } catch (SQLException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    public Player loadSavedGame(int playerId) {
        String query = "SELECT * FROM playerStats WHERE id = ?";
        Player player = null;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                player = new Player();
                player.setId(rs.getInt("id"));
                player.setGpa(rs.getDouble("gpa"));
                player.setEnergy(rs.getInt("energy"));
                player.setStress(rs.getInt("stress"));
                player.setKarma(rs.getInt("karma"));
                player.setXLocation(rs.getInt("xLocation"));
                player.setYLocation(rs.getInt("yLocation"));
                System.out.println("Game loaded for player ID: " + playerId);
            } else {
                System.out.println("No saved game found for player ID: " + playerId);
            }

        } catch (SQLException e) {
            System.err.println("Error loading game: " + e.getMessage());
        }
        return player;
    }
}
