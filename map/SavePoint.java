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
        // SD-UC7: buildCurrentGameState() → gameStateRepository.save(gameState)
        state.GameState gameState = session.buildCurrentGameState();
        boolean saved = session.getSaveStrategy().save(gameState);
        if (saved) {
            System.out.println("Game Saved at Save Point " + savePointId);
        } else {
            System.out.println("Save failed at Save Point " + savePointId);
        }
    }

    public boolean isActive() {
        return true;
    }
}
