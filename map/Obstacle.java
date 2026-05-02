package map;

import controller.GameSession;
import entity.Player;
import interfaces.IInteractable;
import inventory.KeyItem;

public class Obstacle implements IInteractable {
    private int obstacleId;
    private String description;
    private KeyItem requiredItem;
    private boolean isUnlocked;

    public Obstacle(int id, String desc) {
        this.obstacleId = id;
        this.description = desc;
    }

    public void setRequiredItem(KeyItem item) {
        this.requiredItem = item;
    }

    @Override
    public void onInteract(Player player, GameSession session) {
        if (!isUnlocked) {
            if (requiredItem != null && player.getInventory().hasItem(requiredItem.getItemId())) {
                isUnlocked = true;
                System.out.println("Unlocked: " + description);
            } else {
                System.out.println("Locked: " + description + ". " + getHint());
            }
        }
    }

    public boolean tryUnlock(KeyItem item) {
        if (requiredItem != null && requiredItem.getItemId() == item.getItemId()) {
            isUnlocked = true;
            return true;
        }
        return false;
    }

    public String getHint() {
        return "Find the key.";
    }

    public boolean isLocked() {
        return !isUnlocked;
    }
}
