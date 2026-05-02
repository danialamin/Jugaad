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
            return new StatModifierImpl(0.5, -5, -10, 10);
        } else {
            return new StatModifierImpl(-0.5, -10, 20, -5);
        }
    }

    public void incrementAttempt() {
        attempts++;
    }
}
