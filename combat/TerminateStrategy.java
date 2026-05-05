package combat;

import entity.Player;
import entity.Enemy;
import interfaces.ICombatStrategy;

public class TerminateStrategy implements ICombatStrategy {
    private int damageAmount = 30;
    /** SD-UC9 line 74: normal combat karmaChange=-10; SD-UC12 line 102: boss karmaChange=-15. */
    private int karmaEffect;

    private boolean passed = true;

    public TerminateStrategy() {
        // SD-UC9: default normal combat karma penalty = -10
        this.karmaEffect = -10;
    }

    public TerminateStrategy(int karmaEffect) {
        this.karmaEffect = karmaEffect;
    }

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
            // SD-UC9 line 74 / SD-UC12 line 102: victory=true, usedDebug=false, karmaChange, hpChange=0
            return new CombatResult(true, false, getKarmaEffect(), 0);
        } else {
            return new CombatResult(false, false, getKarmaEffect(), passed ? -10 : -15);
        }
    }

    @Override
    public int getKarmaEffect() {
        return karmaEffect;
    }
}
