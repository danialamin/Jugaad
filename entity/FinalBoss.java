package entity;

public class FinalBoss extends Enemy {
    private int phase;
    private int maxPhases = 3;
    private boolean patchDeployed;

    public FinalBoss() {
        this.name = "Final Boss";
        this.hp = 300;
        this.maxHp = 300;
        this.rewardKarma = 50;
        this.phase = 1;
    }

    public void escalatePhase() {
        if (phase < maxPhases) phase++;
    }

    public boolean isMaxPhase() {
        return phase == maxPhases;
    }

    public void deployPatch() {
        patchDeployed = true;
    }

    public int getCurrentPhase() { return phase; }
}
