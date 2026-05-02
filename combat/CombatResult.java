package combat;

public class CombatResult {
    private boolean victory;
    private boolean usedDebug;
    private int karmaChange;
    private int hpChange;

    public CombatResult(boolean victory, boolean usedDebug, int karmaChange, int hpChange) {
        this.victory = victory;
        this.usedDebug = usedDebug;
        this.karmaChange = karmaChange;
        this.hpChange = hpChange;
    }

    public boolean isVictory() { return victory; }
    public boolean wasDebugUsed() { return usedDebug; }
    public int getKarmaChange() { return karmaChange; }
    public int getHPChange() { return hpChange; }
}
