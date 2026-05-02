package map;

import controller.GameSession;
import entity.Player;
import interfaces.IInteractable;

public class SavePoint implements IInteractable {
    private int savePointId;

    public SavePoint(int savePointId) {
        this.savePointId = savePointId;
    }

    @Override
    public void onInteract(Player player, GameSession session) {
        System.out.println("Game Saved at Save Point " + savePointId);
        // Implement save logic through session
    }

    public boolean isActive() {
        return true;
    }
}
