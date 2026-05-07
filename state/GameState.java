package state;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import mode.GameModeType;

public class GameState implements Serializable {
    
    private int saveId;
    private int zoneId;
    private float posX;
    private float posY;
    private GameModeType modeSnapshot;
    
    // Player Stats
    private int hp;
    private int maxHp;
    private double gpa;
    private int energy;
    private int stress;
    private int karma;
    
    private String timestamp;
    private Map<String, Boolean> flags = new HashMap<>();

    public boolean getFlag(String key) {
        return flags.getOrDefault(key, false);
    }

    public void setFlag(String key, boolean val) {
        flags.put(key, val);
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
    
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }
    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }
    public int getStress() { return stress; }
    public void setStress(int stress) { this.stress = stress; }
    public int getKarma() { return karma; }
    public void setKarma(int karma) { this.karma = karma; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public Map<String, Boolean> getFlags() { return flags; }
    public void setFlags(Map<String, Boolean> flags) { this.flags = flags; }
}
