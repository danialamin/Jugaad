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
            return new CombatResult(true, true, getKarmaEffect(), 0);
        } else {
            // SD-UC10: wrong answer → zombie attacks → player takes 10 HP damage
            return new CombatResult(false, true, -2, -10);
        }
    }

    @Override
    public int getKarmaEffect() {
        return 10; // Large karma boost for debugging
    }
}
