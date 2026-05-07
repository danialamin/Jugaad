package database;

import state.GameState;
import mode.GameModeType;
import java.sql.*;

public class SQLServerHandler implements PersistenceHandler {

    private static final String URL = 
        "jdbc:sqlserver://localhost:1433;databaseName=campusFlexDb;encrypt=true;trustServerCertificate=true;";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "CampusFlex123!";

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC Driver not found. Ensure mssql-jdbc JAR is on the classpath.", e);
        }
        return DriverManager.getConnection(URL, DB_USER, DB_PASS);
    }

    private void ensureTableExists() {
        String createTableSQL = 
            "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='game_state' AND xtype='U') " +
            "CREATE TABLE game_state (" +
            "  saveId INT PRIMARY KEY, " +
            "  posX FLOAT, " +
            "  posY FLOAT, " +
            "  currentZoneName VARCHAR(50), " +
            "  modeSnapshot VARCHAR(50), " +
            "  zombieMode BIT, " +
            "  phaseOneState VARCHAR(50), " +
            "  phaseTwoState VARCHAR(50), " +
            "  phase2IntroPart INT, " +
            "  defeatedZombies VARCHAR(500), " +
            "  serverBossHp INT, " +
            "  prayedThisPhase BIT, " +
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
            "  posX=?, posY=?, currentZoneName=?, modeSnapshot=?, zombieMode=?, " +
            "  phaseOneState=?, phaseTwoState=?, phase2IntroPart=?, defeatedZombies=?, " +
            "  serverBossHp=?, prayedThisPhase=?, hp=?, maxHp=?, gpa=?, energy=?, stress=?, karma=?, timestamp=? " +
            "WHEN NOT MATCHED THEN INSERT " +
            "  (saveId, posX, posY, currentZoneName, modeSnapshot, zombieMode, " +
            "   phaseOneState, phaseTwoState, phase2IntroPart, defeatedZombies, " +
            "   serverBossHp, prayedThisPhase, hp, maxHp, gpa, energy, stress, karma, timestamp) " +
            "  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(mergeSQL)) {

            String modeStr = state.getModeSnapshot() != null ? state.getModeSnapshot().name() : "NORMAL";
            String ts = state.getTimestamp() != null ? state.getTimestamp() : new java.util.Date().toString();
            String zoneName = state.getCurrentZoneName() != null ? state.getCurrentZoneName() : "LIBRARY";
            String defeatedStr = String.join(",", state.getDefeatedZombies());

            // USING clause
            pstmt.setInt(1, state.getSaveId());
            // UPDATE SET clause (18 params)
            pstmt.setFloat(2, state.getPosX());
            pstmt.setFloat(3, state.getPosY());
            pstmt.setString(4, zoneName);
            pstmt.setString(5, modeStr);
            pstmt.setBoolean(6, state.isZombieMode());
            pstmt.setString(7, state.getPhaseOneState() != null ? state.getPhaseOneState() : "CS_CLASS_REQUIRED");
            pstmt.setString(8, state.getPhaseTwoState() != null ? state.getPhaseTwoState() : "CUTSCENE");
            pstmt.setInt(9, state.getPhase2IntroPart());
            pstmt.setString(10, defeatedStr);
            pstmt.setInt(11, state.getServerBossHp());
            pstmt.setBoolean(12, state.isPrayedThisPhase());
            pstmt.setInt(13, state.getHp());
            pstmt.setInt(14, state.getMaxHp());
            pstmt.setDouble(15, state.getGpa());
            pstmt.setInt(16, state.getEnergy());
            pstmt.setInt(17, state.getStress());
            pstmt.setInt(18, state.getKarma());
            pstmt.setString(19, ts);
            // INSERT VALUES clause (19 params)
            pstmt.setInt(20, state.getSaveId());
            pstmt.setFloat(21, state.getPosX());
            pstmt.setFloat(22, state.getPosY());
            pstmt.setString(23, zoneName);
            pstmt.setString(24, modeStr);
            pstmt.setBoolean(25, state.isZombieMode());
            pstmt.setString(26, state.getPhaseOneState() != null ? state.getPhaseOneState() : "CS_CLASS_REQUIRED");
            pstmt.setString(27, state.getPhaseTwoState() != null ? state.getPhaseTwoState() : "CUTSCENE");
            pstmt.setInt(28, state.getPhase2IntroPart());
            pstmt.setString(29, defeatedStr);
            pstmt.setInt(30, state.getServerBossHp());
            pstmt.setBoolean(31, state.isPrayedThisPhase());
            pstmt.setInt(32, state.getHp());
            pstmt.setInt(33, state.getMaxHp());
            pstmt.setDouble(34, state.getGpa());
            pstmt.setInt(35, state.getEnergy());
            pstmt.setInt(36, state.getStress());
            pstmt.setInt(37, state.getKarma());
            pstmt.setString(38, ts);

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
                state.setPosX(rs.getFloat("posX"));
                state.setPosY(rs.getFloat("posY"));
                state.setCurrentZoneName(rs.getString("currentZoneName"));
                state.setModeSnapshot(GameModeType.valueOf(rs.getString("modeSnapshot")));
                state.setZombieMode(rs.getBoolean("zombieMode"));
                state.setPhaseOneState(rs.getString("phaseOneState"));
                state.setPhaseTwoState(rs.getString("phaseTwoState"));
                state.setPhase2IntroPart(rs.getInt("phase2IntroPart"));
                
                String defeatedStr = rs.getString("defeatedZombies");
                java.util.Set<String> defeated = new java.util.HashSet<>();
                if (defeatedStr != null && !defeatedStr.isEmpty()) {
                    for (String s : defeatedStr.split(",")) {
                        defeated.add(s.trim());
                    }
                }
                state.setDefeatedZombies(defeated);
                
                state.setServerBossHp(rs.getInt("serverBossHp"));
                state.setPrayedThisPhase(rs.getBoolean("prayedThisPhase"));
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
