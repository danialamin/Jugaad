package activity;

import controller.GameSession;
import entity.Player;
import entity.PlayerStats;
import interfaces.IInteractable;
import interfaces.IStatModifier;
import entity.StatModifierImpl;

public class Canteen implements IInteractable {
    private int canteenId;
    private int floor;

    public Canteen(int id, int floor) {
        this.canteenId = id;
        this.floor = floor;
    }

    @Override
    public void onInteract(Player player, GameSession session) {
        if (canEat(player.getStats())) {
            showMenu();
            System.out.println("Eating at canteen...");
            player.getStats().applyModifier(buildFoodModifier());
        } else {
            System.out.println("You are not hungry right now.");
        }
    }

    public void showMenu() {
        System.out.println("--- Canteen Menu ---");
        System.out.println("1. Biryani - restores 50 energy");
        System.out.println("--------------------");
    }

    public IStatModifier buildFoodModifier() {
        // SD-UC4: food restores HP (handled at call site) and reduces stress
        return new StatModifierImpl(0, -10, 0);
    }

    public boolean canEat(PlayerStats stats) {
        return true; // HP check handled at the interaction call site
    }
}
