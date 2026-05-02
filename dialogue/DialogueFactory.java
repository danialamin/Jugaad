package dialogue;

import state.KarmaTracker;
import mode.GameModeType;

public class DialogueFactory {
    public Dialogue loadDialogue(int npcId, GameModeType mode) {
        Dialogue d = new Dialogue(1, "Welcome to CampusFlex!");
        d.addOption(new DialogueOption("Thanks!", 5, true));
        d.addOption(new DialogueOption("Whatever.", -5, false));
        return d;
    }

    public void applyEffect(DialogueOption opt, KarmaTracker k) {
        if (opt.getKarmaEffect() > 0) {
            k.add(opt.getKarmaEffect(), "Made a kind choice");
        } else if (opt.getKarmaEffect() < 0) {
            k.deduct(Math.abs(opt.getKarmaEffect()), "Made a rude choice");
        }
    }

    public boolean hasEasterEgg(int npcId) {
        return npcId == 999;
    }
}
