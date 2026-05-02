package dialogue;

import mode.GameModeType;
import state.KarmaTracker;

public class DialogueManager {
    private final DialogueFactory factory;

    public DialogueManager() {
        this.factory = new DialogueFactory();
    }

    public Dialogue loadDialogue(int npcId, GameModeType mode) {
        // Gray-structure placeholder: delegate and/or post-process by context.
        return factory.loadDialogue(npcId, mode);
    }

    public void applyEffect(DialogueOption opt, KarmaTracker k) {
        // Gray-structure placeholder: centralize dialogue effect application.
        factory.applyEffect(opt, k);
    }

    public boolean hasEasterEgg(int npcId) {
        // Gray-structure placeholder: route special dialogue checks.
        return factory.hasEasterEgg(npcId);
    }
}
