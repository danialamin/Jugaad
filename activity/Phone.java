package activity;

import controller.GameSession;
import interfaces.IStatModifier;
import entity.StatModifierImpl;

public class Phone {
    private boolean isOpen;

    public void open() { isOpen = true; }
    public void close() { isOpen = false; }

    public IStatModifier buildSocialMediaModifier() {
        return new StatModifierImpl(0, -5, -10, 0);
    }

    public IStatModifier buildCheatModifier() {
        return new StatModifierImpl(0.5, -20, 30, -20);
    }

    public boolean isDuringQuiz(GameSession session) {
        return false; // Placeholder
    }
}
