package combat;

import entity.Player;
import entity.Enemy;
import interfaces.ICombatStrategy;

public class DebugStrategy implements ICombatStrategy {
    private CombatChallenge challenge;

    public DebugStrategy(CombatChallenge challenge) {
        this.challenge = challenge;
    }

    @Override
    public CombatResult execute(Player p, Enemy e) {
        System.out.println("Executing Debug Strategy...");
        challenge.present();
        boolean win = challenge.evaluate("4"); // Auto-win for now
        
        if (win) {
            e.takeDamage(e.getHp()); // Instakill
            return new CombatResult(true, true, getKarmaEffect(), 0);
        } else {
            p.takeDamage(10);
            return new CombatResult(false, true, -5, -10);
        }
    }

    @Override
    public int getKarmaEffect() {
        return 5;
    }
}
