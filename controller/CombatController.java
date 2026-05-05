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

    public CombatController() {
        // Deferred player binding — setPlayer() called after session init
    }

    public CombatController(Player player) {
        this.player = player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void initiateCombat(Enemy enemy) {
        this.currentEnemy = enemy;
    }

    public void setStrategy(ICombatStrategy s) {
        this.strategy = s;
    }

    public CombatResult executeCombatMove(CombatMove move, boolean passedMinigame) {
        return executeCombatMove(move, passedMinigame, false);
    }

    /** @param isBossFight SD-UC12: Terminate against boss uses -15 karma instead of -10. */
    public CombatResult executeCombatMove(CombatMove move, boolean passedMinigame, boolean isBossFight) {
        CombatStrategyFactory factory = new CombatStrategyFactory();
        this.strategy = factory.createStrategy(move.name(), isBossFight);
        
        if (this.strategy instanceof combat.DebugStrategy) {
            ((combat.DebugStrategy) this.strategy).setPassed(passedMinigame);
        } else if (this.strategy instanceof combat.TerminateStrategy) {
            ((combat.TerminateStrategy) this.strategy).setPassed(passedMinigame);
        }
        
        return this.strategy.execute(player, currentEnemy);
    }

    public CombatResult executeCombatMove(CombatMove move) {
        return executeCombatMove(move, false, false);
    }

    public void applyResult(CombatResult result) {
        // Results are currently applied dynamically by the GamePanel logic 
        // to handle UI state transitions and map updates immediately.
    }

    public boolean isPlayerAlive() {
        // Gray-structure placeholder: defensive checks + player state query.
        return player != null && player.isAlive();
    }
}
