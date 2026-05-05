package activity;

import controller.GameSession;
import entity.Player;
import interfaces.IInteractable;
import interfaces.IStatModifier;
import entity.StatModifierImpl;

public class Quiz {
    private int quizId;
    private String question = "What is OOP?";
    private String correctAnswer = "Object Oriented Programming";
    private int timeLimit = 30;
    private int attempts = 0;

    public boolean evaluate(String answer) {
        attempts++;
        return correctAnswer.equalsIgnoreCase(answer);
    }

    public boolean isExpired(int elapsed) {
        return elapsed > timeLimit;
    }

    public IStatModifier buildResultModifier(boolean correct) {
        if (correct) {
            // SD-UC2: gpaDelta=+0.3, stressDelta=0, karmaDelta=+5
            return new StatModifierImpl(0.3, 0, 5);
        } else {
            // SD-UC2: gpaDelta=+0.1, stressDelta=0, karmaDelta=0
            return new StatModifierImpl(0.1, 0, 0);
        }
    }

    public void incrementAttempt() {
        attempts++;
    }
}
