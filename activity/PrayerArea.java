package activity;

import controller.GameSession;
import entity.Player;
import interfaces.IInteractable;
import interfaces.IStatModifier;
import entity.StatModifierImpl;

public class PrayerArea implements IInteractable {
    private int floor;
    private boolean prayedThisCycle;

    public PrayerArea(int floor) {
        this.floor = floor;
    }

    @Override
    public void onInteract(Player player, GameSession session) {
        if (isAvailable()) {
            System.out.println("Praying...");
            player.getStats().applyModifier(buildPrayerModifier());
            prayedThisCycle = true;
        } else {
            System.out.println("You have already prayed.");
        }
    }

    public boolean isAvailable() {
        return !prayedThisCycle;
    }

    public IStatModifier buildPrayerModifier() {
        // SD-UC5: gpaDelta=0, stressDelta=0, karmaDelta=+20
        return new StatModifierImpl(0, 0, 20);
    }

    public void resetCycle() {
        prayedThisCycle = false;
    }
}
