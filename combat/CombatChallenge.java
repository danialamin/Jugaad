package combat;

import interfaces.IStatModifier;
import entity.StatModifierImpl;

public class CombatChallenge {
    private int challengeId;
    private String question = "What is 2+2?";
    private String correctAnswer = "4";
    private int timeLimit = 10;

    public void present() {
        System.out.println("Combat Challenge: " + question);
    }

    public boolean evaluate(String answer) {
        return correctAnswer.equals(answer);
    }

    public IStatModifier buildPenaltyModifier() {
        return new StatModifierImpl(0, -10, 5, 0);
    }
}
