package entity;

import controller.GameSession;
import interfaces.IInteractable;

public class NPC implements IInteractable {
    private int npcId;
    private String name;
    private String role;
    private int hostilityThreshold;
    private int interactionCount;

    @Override
    public void onInteract(Player player, GameSession session) {
        interactionCount++;
        System.out.println(name + ": Hello there!");
    }

    public boolean isHostile(int karma) {
        return karma < hostilityThreshold;
    }

    public void incrementInteraction() {
        interactionCount++;
    }
}
