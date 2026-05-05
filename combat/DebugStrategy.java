package combat;

import entity.Player;
import entity.Enemy;
import interfaces.ICombatStrategy;

public class DebugStrategy implements ICombatStrategy {
    private boolean passedQuiz = false;

    // Optional: Keep old constructor if Factory needs it
    public DebugStrategy() {}
    public DebugStrategy(CombatChallenge c) {}

    public void setPassed(boolean passed) {
        this.passedQuiz = passed;
    }

    public CombatResult execute(Player p, Enemy e) {
        if (passedQuiz) {
            e.takeDamage(e.getHp()); // Instakill on correct answer
            // SD-UC9 line 46 / SD-UC10 line 45: victory=true, usedDebug=true, karmaChange=+15, hpChange=0
            return new CombatResult(true, true, getKarmaEffect(), 0);
        } else {
            // SD-UC10 line 62: victory=false, usedDebug=true, karmaChange=0, hpChange=-10
            return new CombatResult(false, true, 0, -10);
        }
    }

    @Override
    public int getKarmaEffect() {
        // SD-UC9 line 46 / SD-UC10 line 45: karmaChange=+15
        return 15;
    }
}
