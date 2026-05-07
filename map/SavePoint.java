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
        // SD-UC7: handle save interaction
        session.saveGame();
        System.out.println("Game Save initiated at Save Point " + savePointId);
    }

    public boolean isActive() {
        return true;
    }
}
