package combat;

import entity.Player;
import entity.Enemy;
import interfaces.ICombatStrategy;

public class TerminateStrategy implements ICombatStrategy {
    private int damageAmount = 30;

    private boolean passed = true;

    public void setPassed(boolean p) {
        this.passed = p;
    }

    @Override
    public CombatResult execute(Player p, Enemy e) {
        System.out.println("Executing Terminate Strategy...");
        
        if (passed) {
            e.takeDamage(damageAmount);
        } else {
            System.out.println("Terminate attack missed!");
        }
        
        if (e.isDefeated()) {
            return new CombatResult(true, false, getKarmaEffect(), 0);
        } else {
            // SD-UC9: damage reported via hpChange, applied by caller (no direct attack here)
            return new CombatResult(false, false, getKarmaEffect(), passed ? -10 : -15);
        }
    }

    @Override
    public int getKarmaEffect() {
        return -5; // Aggressive move
    }
}
