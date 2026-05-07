package database;

import state.GameState;
import mode.GameModeType;
import java.sql.*;

public class SQLServerHandler implements PersistenceHandler {

    private static final String URL = 
        "jdbc:sqlserver://localhost:1433;databaseName=campusFlexDb;encrypt=true;trustServerCertificate=true;integratedSecurity=true;";

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC Driver not found. Ensure mssql-jdbc JAR is on the classpath.", e);
        }
        return DriverManager.getConnection(URL);
    }

    private void ensureTableExists() {
        String createTableSQL = 
            "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='game_state' AND xtype='U') " +
            "CREATE TABLE game_state (" +
            "  saveId INT PRIMARY KEY, " +
            "  zoneId INT, " +
            "  posX FLOAT, " +
            "  posY FLOAT, " +
            "  modeSnapshot VARCHAR(50), " +
            "  hp INT, " +
            "  maxHp INT, " +
            "  gpa FLOAT, " +
            "  energy INT, " +
            "  stress INT, " +
            "  karma INT, " +
            "  timestamp VARCHAR(100)" +
            ")";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean save(GameState state) {
        ensureTableExists();
        String mergeSQL = 
            "MERGE game_state AS target " +
            "USING (SELECT ? AS saveId) AS source ON target.saveId = source.saveId " +
            "WHEN MATCHED THEN UPDATE SET " +
            "  zoneId=?, posX=?, posY=?, modeSnapshot=?, hp=?, maxHp=?, gpa=?, energy=?, stress=?, karma=?, timestamp=? " +
            "WHEN NOT MATCHED THEN INSERT " +
            "  (saveId, zoneId, posX, posY, modeSnapshot, hp, maxHp, gpa, energy, stress, karma, timestamp) " +
            "  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(mergeSQL)) {

            String modeStr = state.getModeSnapshot() != null ? state.getModeSnapshot().name() : "NORMAL";
            String ts = state.getTimestamp() != null ? state.getTimestamp() : new java.util.Date().toString();

            // USING clause
            pstmt.setInt(1, state.getSaveId());
            // UPDATE SET clause
            pstmt.setInt(2, state.getZoneId());
            pstmt.setFloat(3, state.getPosX());
            pstmt.setFloat(4, state.getPosY());
            pstmt.setString(5, modeStr);
            pstmt.setInt(6, state.getHp());
            pstmt.setInt(7, state.getMaxHp());
            pstmt.setDouble(8, state.getGpa());
            pstmt.setInt(9, state.getEnergy());
            pstmt.setInt(10, state.getStress());
            pstmt.setInt(11, state.getKarma());
            pstmt.setString(12, ts);
            // INSERT VALUES clause
            pstmt.setInt(13, state.getSaveId());
            pstmt.setInt(14, state.getZoneId());
            pstmt.setFloat(15, state.getPosX());
            pstmt.setFloat(16, state.getPosY());
            pstmt.setString(17, modeStr);
            pstmt.setInt(18, state.getHp());
            pstmt.setInt(19, state.getMaxHp());
            pstmt.setDouble(20, state.getGpa());
            pstmt.setInt(21, state.getEnergy());
            pstmt.setInt(22, state.getStress());
            pstmt.setInt(23, state.getKarma());
            pstmt.setString(24, ts);

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public GameState load(int saveId) {
        ensureTableExists();
        String sql = "SELECT * FROM game_state WHERE saveId = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, saveId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                GameState state = new GameState();
                state.setSaveId(rs.getInt("saveId"));
                state.setZoneId(rs.getInt("zoneId"));
                state.setPosX(rs.getFloat("posX"));
                state.setPosY(rs.getFloat("posY"));
                String modeStr = rs.getString("modeSnapshot");
                state.setModeSnapshot(GameModeType.valueOf(modeStr));
                state.setHp(rs.getInt("hp"));
                state.setMaxHp(rs.getInt("maxHp"));
                state.setGpa(rs.getDouble("gpa"));
                state.setEnergy(rs.getInt("energy"));
                state.setStress(rs.getInt("stress"));
                state.setKarma(rs.getInt("karma"));
                state.setTimestamp(rs.getString("timestamp"));
                return state;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
