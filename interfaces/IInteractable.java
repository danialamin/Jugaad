package interfaces;

import entity.Player;
import controller.GameSession;

public interface IInteractable {
    void onInteract(Player player, GameSession session);
}
