package state;

import java.util.HashMap;
import java.util.Map;
import mode.GameModeType; // We will create this enum

public class GameState {
    private int saveId;
    private int zoneId;
    private float posX;
    private float posY;
    private GameModeType modeSnapshot;
    private Map<String, Boolean> flags = new HashMap<>();
    private String timestamp;

    public boolean getFlag(String key) {
        return flags.getOrDefault(key, false);
    }

    public void setFlag(String key, boolean val) {
        flags.put(key, val);
    }

    public String serialize() {
        return "{}"; // Placeholder
    }

    public static GameState deserialize(String data) {
        return new GameState(); // Placeholder
    }

    public int getSaveId() { return saveId; }
    public void setSaveId(int saveId) { this.saveId = saveId; }
    public int getZoneId() { return zoneId; }
    public void setZoneId(int zoneId) { this.zoneId = zoneId; }
    public float getPosX() { return posX; }
    public void setPosX(float posX) { this.posX = posX; }
    public float getPosY() { return posY; }
    public void setPosY(float posY) { this.posY = posY; }
    public GameModeType getModeSnapshot() { return modeSnapshot; }
    public void setModeSnapshot(GameModeType modeSnapshot) { this.modeSnapshot = modeSnapshot; }
}
