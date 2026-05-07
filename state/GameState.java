package state;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import mode.GameModeType;

public class GameState implements Serializable {
    
    private int saveId;
    private float posX;
    private float posY;
    private GameModeType modeSnapshot;
    
    // Current zone (as String to avoid serialization issues with enum)
    private String currentZoneName;
    
    // Story Phase State
    private boolean zombieMode;
    private String phaseOneState;   // e.g. "CS_CLASS_REQUIRED", "PHASE_1_DONE"
    private String phaseTwoState;   // e.g. "EXPLORE", "IN_COMBAT"
    private int phase2IntroPart;
    private Set<String> defeatedZombies = new HashSet<>();
    private int serverBossHp;
    private boolean prayedThisPhase;
    
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
    public float getPosX() { return posX; }
    public void setPosX(float posX) { this.posX = posX; }
    public float getPosY() { return posY; }
    public void setPosY(float posY) { this.posY = posY; }
    public GameModeType getModeSnapshot() { return modeSnapshot; }
    public void setModeSnapshot(GameModeType modeSnapshot) { this.modeSnapshot = modeSnapshot; }
    
    public String getCurrentZoneName() { return currentZoneName; }
    public void setCurrentZoneName(String currentZoneName) { this.currentZoneName = currentZoneName; }
    
    public boolean isZombieMode() { return zombieMode; }
    public void setZombieMode(boolean zombieMode) { this.zombieMode = zombieMode; }
    public String getPhaseOneState() { return phaseOneState; }
    public void setPhaseOneState(String phaseOneState) { this.phaseOneState = phaseOneState; }
    public String getPhaseTwoState() { return phaseTwoState; }
    public void setPhaseTwoState(String phaseTwoState) { this.phaseTwoState = phaseTwoState; }
    public int getPhase2IntroPart() { return phase2IntroPart; }
    public void setPhase2IntroPart(int phase2IntroPart) { this.phase2IntroPart = phase2IntroPart; }
    public Set<String> getDefeatedZombies() { return defeatedZombies; }
    public void setDefeatedZombies(Set<String> defeatedZombies) { this.defeatedZombies = defeatedZombies; }
    public int getServerBossHp() { return serverBossHp; }
    public void setServerBossHp(int serverBossHp) { this.serverBossHp = serverBossHp; }
    public boolean isPrayedThisPhase() { return prayedThisPhase; }
    public void setPrayedThisPhase(boolean prayedThisPhase) { this.prayedThisPhase = prayedThisPhase; }

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
