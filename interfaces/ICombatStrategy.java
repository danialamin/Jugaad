package interfaces;

import entity.Player;
import entity.Enemy;
import combat.CombatResult;

public interface ICombatStrategy {
    CombatResult execute(Player p, Enemy e);
    int getKarmaEffect();
}
