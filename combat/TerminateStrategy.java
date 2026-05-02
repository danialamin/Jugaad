package combat;

import entity.Player;
import entity.Enemy;
import interfaces.ICombatStrategy;

public class TerminateStrategy implements ICombatStrategy {
    private int damageAmount = 30;

    @Override
    public CombatResult execute(Player p, Enemy e) {
        System.out.println("Executing Terminate Strategy...");
        e.takeDamage(damageAmount);
        
        if (e.isDefeated()) {
            return new CombatResult(true, false, getKarmaEffect(), 0);
        } else {
            e.attack(p);
            return new CombatResult(false, false, 0, -10);
        }
    }

    @Override
    public int getKarmaEffect() {
        return -5; // Aggressive move
    }
}
