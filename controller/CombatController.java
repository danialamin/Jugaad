package controller;

import combat.CombatResult;
import entity.Enemy;
import entity.Player;
import interfaces.ICombatStrategy;

public class CombatController {
    private Player player;
    private Enemy currentEnemy;
    private ICombatStrategy strategy;

    public void initiateCombat(Enemy enemy) {
        this.currentEnemy = enemy;
    }

    public void setStrategy(ICombatStrategy s) {
        this.strategy = s;
    }

    public CombatResult executeCombatMove(combat.CombatMove move) {
        if (strategy != null && currentEnemy != null && player != null) {
            // Assume the strategy interprets the move (this is a placeholder implementation)
            return strategy.execute(player, currentEnemy);
        }
        return null;
    }

    public void applyResult(CombatResult result) {
        if (result.isVictory()) {
            System.out.println("Enemy defeated!");
        } else {
            System.out.println("Player was defeated.");
        }
    }

    public boolean isPlayerAlive() {
        return player.isAlive();
    }
}
