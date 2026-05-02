package controller;

import combat.CombatMove;
import combat.CombatResult;
import combat.CombatStrategyFactory;
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

    public CombatResult executeCombatMove(CombatMove move) {
        // Gray-structure placeholder: wire strategy selection and combat flow here.
        CombatStrategyFactory factory = new CombatStrategyFactory();
        this.strategy = factory.createStrategy(move.name());
        return null;
    }

    public CombatResult executeCombatMove() {
        // Backward-compatible overload; prefer executeCombatMove(CombatMove).
        return executeCombatMove(CombatMove.TERMINATE);
    }

    public void applyResult(CombatResult result) {
        // Gray-structure placeholder: apply HP/karma and state transition effects here.
    }

    public boolean isPlayerAlive() {
        // Gray-structure placeholder: defensive checks + player state query.
        return player != null && player.isAlive();
    }
}
